package com.example.sstubot.initial;

import com.example.sstubot.database.model.*;
import com.example.sstubot.database.model.urils.ClaimType;
import com.example.sstubot.database.model.urils.EducationType;
import com.example.sstubot.database.service.DirectionService;
import com.example.sstubot.database.service.UserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Scope(value = "prototype")
public class LoadManager
{
    private DirectionService directionService;
    private Map<String, User> userMap;
    final String URL_DOMAIN_PAGE = "https://abitur.sstu.ru";
    Map<String, Direction> directionHashMap;
    @Autowired
    public LoadManager(DirectionService directionService)
    {
        this.directionService = directionService;
    }
    @Transactional
    public Map<String, User> loadClaims(Map<String, Direction> directionHashMap, List<Direction> directions) throws IOException {
        this.userMap = new HashMap<>();
        this.directionHashMap = directionHashMap;
        List<Direction> directionList = directions;
        try
        {
            loadWrapper(directionList);
        }
        catch (IOException err)
        {
            throw err;
        }
        return this.userMap;
    }

    private void loadWrapper(List<Direction> directionList) throws IOException {
        int a = 0;
        for(Direction direction : directionList)
        {
            a++;
            if(!direction.isIgnoreDirection())
            {
                fillClaimsIntoDirection(direction);
            }
        }
    }

    //Загрузка уникальных юзеров со страницы списков (как бюджет, так и коммерция)
    private void fillClaimsIntoDirection(Direction direction) throws IOException {
        //MetaInfoAboutUserIntoDirection metaInfo = direction.getMetaInfo();
        String urlToBudgetClaim;
        //Заполнение Claim бюджет
        if(validateURLtoClaims(direction.getUrlToListOfClaims()))
        {
            urlToBudgetClaim = direction.getUrlToListOfClaims();
            Document document = Jsoup.connect(urlToBudgetClaim).get();
            //Каждый блок содержит название таблицы и саму таблицу
            Elements divTables = document.select("div.rasp-block");

            for(Element divTable : divTables)
            {
                // TODO: 07.05.2023 определить какая это таблица
                //Таблица по целевому,общему и специальному направлению
                String tableName = divTable.select("div.block-title div").get(1).ownText().trim();
                //Element divTableName = divTable.select("div.block-title div").get(1);
                System.out.println(tableName);
                //String tableName = divTableName != null ? tableName : null ;
                ClaimType claimType = defineClaimType(tableName);
                Element tableTBody = divTable.selectFirst("tbody");
                if(tableTBody == null)
                    continue;
                fillClaimIntoDirectionFromTbody(direction, tableTBody,claimType);
            }
        }
        //Заполение Claim на коммерцию
        if(validateURLtoClaims(direction.getUrlToListOfClaimsCommerce()))
        {
            Document documentForCommerce = Jsoup.connect(direction.getUrlToListOfClaimsCommerce()).get();
            Element divRaspBlock = documentForCommerce.selectFirst("div.rasp-block");
            if(divRaspBlock != null)
            {
                String tableName = divRaspBlock.select("div.block-title div").get(1).ownText().trim();
                if(!tableName.matches(".*платной.*"))
                    throw new RemoteException("Не распознано название таблицы : " + tableName);
                Element tbody = divRaspBlock.getElementsByTag("tbody").first();
                if(tbody != null)
                {
                    fillClaimIntoDirectionFromTbody(direction, tbody,ClaimType.COMMERCE_GENERAL_LIST);
                }
            }
        }
        //По договорам

        //User user = loadRawUserFromTableString()
    }
    private ClaimType defineClaimType(String tableName)
    {
        ClaimType claimType;
        if(tableName == null || tableName.isBlank() || tableName.isBlank())
        {
            throw new RuntimeException("Каждая таблица должна содержать имя.");
        }
        else if(tableName.matches(".*целевое.*"))
        {
            claimType = ClaimType.BUDGET_TARGET_QUOTA;
        }
        else if(tableName.matches(".*специальной.*"))
        {
            claimType = ClaimType.BUDGET_SPECIAL_QUOTA;
        }
        else if(tableName.matches(".*особые права.*"))
        {
            claimType = ClaimType.BUDGET_UNUSUAL_QUOTA;
        }
        else if(tableName.matches(".*общему конкурсу.*"))
        {
            claimType = ClaimType.BUDGET_GENERAL_LIST;
        }
        else if(tableName.matches(".*На платной основе.*"))
        {
            claimType = ClaimType.COMMERCE_GENERAL_LIST;
        }
        else
        {
            throw new RuntimeException("Не распознано имя таблицы : " + tableName);
        }
        return claimType;
    }

    private boolean validateURLtoClaims(String url)
    {
        return url != null && !url.isEmpty() && !url.isBlank() ? true : false;
    }

    private void fillClaimIntoDirectionFromTbody(Direction direction, Element tbody, ClaimType claimType)
    {
        Elements rawClaimList = tbody.children();
        for(Element rawClaimData : rawClaimList)
        {
            loadRawClaim(direction, rawClaimData.children(),claimType);
        }
    }

    /**
     * @param rawDataClaim Группа элементов-столбцов, олицетворяющих User's в таблице (дочерние элементы tr  таблице)
     * @return User
     */
    private void loadRawClaim(Direction currentDirection, Elements rawDataClaim, ClaimType claimType)
    {
        if((rawDataClaim.size() - 1) < currentDirection.getMetaInfo().CHAMPION_ID)
        {
            //System.out.println("Не удалось распарсить юзера : " + userCode + " по ссылке " + currentDirection.getUrlToListOfClaims());
            return;
        }
        String userCode = rawDataClaim.get(MetaInfoAboutUserIntoDirection.USER_CODE_ID).ownText().trim();
        User user = null;
        //User-id найден впервые -> создаем юзера, заявку и список приоретета
        if(!this.userMap.containsKey(userCode))
        {
            String statusOfClaim = rawDataClaim.get(currentDirection.getMetaInfo().CONDITION_ID).text();
            //String typeOfDocument = rawDataClaim.get(currentDirection.getMetaInfo().CONDITION_ID).text();
            Pattern pattern = Pattern.compile("(.*Подано.*)|(.*Зачислен.*)",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(statusOfClaim);
            boolean isActualClaim = matcher.matches();
            if(!isActualClaim)
                return;
            String typeDocument = rawDataClaim.get(currentDirection.getMetaInfo().DOCUMENT_TYPE_ID).ownText().trim();
            boolean originalDoc = typeDocument.matches(".*Оригинал.*");
            user = new User();
            user.setUniqueCode(userCode);
            user.setOriginalDocuments(originalDoc);
            Elements rawPriorities = rawDataClaim.get(MetaInfoAboutUserIntoDirection.USER_CODE_ID).getElementsByTag("a");
            for(int i = 0;i < rawPriorities.size();i++)
            {
                Element p = rawPriorities.get(i);
                String infoAboutAnotherClaimIntoTitle = p.attr("title");
                boolean isBudget = infoAboutAnotherClaimIntoTitle.matches(".*бюджет.*");
                EducationType edyType = defineEduTypeByString(infoAboutAnotherClaimIntoTitle);
                Direction direction;
                String url = URL_DOMAIN_PAGE + p.attr("href").trim();
                direction = directionHashMap.get(url);
                ClaimPriorities priority = new ClaimPriorities(direction,user,isBudget);
                user.addPriorities(priority);
            }
            formedClaimAndAddIntoInsitute(currentDirection, rawDataClaim, claimType, user);
            this.userMap.put(userCode,user);
        }
        else
        {
            user = userMap.get(userCode);
            String statusOfClaim = rawDataClaim.get(currentDirection.getMetaInfo().CONDITION_ID).ownText().trim();
            System.out.println("user :" + user.getUniqueCode() + " url: " + currentDirection.getUrlToListOfClaims());
            Pattern pattern = Pattern.compile("(.*Подано.*)|(.*Зачислен.*)",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(statusOfClaim);
            boolean isActualClaim = matcher.matches();
            if(!isActualClaim )
                return;
            formedClaimAndAddIntoInsitute(currentDirection, rawDataClaim, claimType, user);
        }


    }
    private EducationType defineEduTypeByString(String str)
    {
        EducationType eduType;
        if(str.matches(".*Очная\\s+форма.*"))
            eduType = EducationType.OChNAYa;
        else if(str.matches(".*Заочная\\s+форма.*"))
            eduType = EducationType.ZAOChNAYa;
        else if(str.matches(".*Очно-заочная.*"))
            eduType = EducationType.OChNO_ZAOChNAYa;
        else
            throw new RuntimeException("Не удалось распознать форму обучения (очная | заочная | очно-зачоная форма)");
        return eduType;
    }
    private void formedClaimAndAddIntoInsitute(Direction currentDirection, Elements rawUserData, ClaimType claimType, User user) {
        List<Exam> exams = currentDirection.getMetaInfo().getExamList();
        Claim claim = Claim.createNewClaim(user,currentDirection,claimType);
        List<Score> scores = new LinkedList<>();
        MetaInfoAboutUserIntoDirection metaInf = currentDirection.getMetaInfo();
        int id = 0;
        for(int i = MetaInfoAboutUserIntoDirection.AMOUNT_SCORE_ID + 1;i < metaInf.AMOUNT_SCORE_FOR_INDIVIDUAL_ACHIEVEMENTS;i++)
        {
            int points = 0;
            Score score = new Score();
            score.setExam(exams.get(id));
            score.setClaim(claim);
            scores.add(score);
            try
            {
                points = Integer.valueOf(rawUserData.get(i).text().trim());
            }
            catch (NumberFormatException err)
            {
                System.out.println("Ошибка преобразования кол-во баллов: " + currentDirection.getUrlToListOfClaims() + "\nСписок :"
                + currentDirection.getName() + "\nПользователь: " + user.getUniqueCode()
                );
                score.setAbsence(true);
            }
            score.setScore(points);
            id++;
        }
        claim.setScoreList(scores);
        String individualAchievements = rawUserData.get(metaInf.AMOUNT_SCORE_FOR_INDIVIDUAL_ACHIEVEMENTS).text();
        int individualScore = 0;
        String sumScoreRawStr = rawUserData.get(MetaInfoAboutUserIntoDirection.AMOUNT_SCORE_ID).ownText().trim();
        int sumScore = 0;
        try
        {
            sumScore = Integer.valueOf(sumScoreRawStr);

        }
        catch (NumberFormatException err)
        {
            System.out.println("Ошибка приведения к числовому виду (сумма баллов)");
        }
        claim.setSummaryOfScore(sumScore);

        try
        {
            individualScore = Integer.valueOf(individualAchievements);
        }
        catch (NumberFormatException err)
        {
            System.out.println("Ошибка приведения к чсловому виду (индивидуальные достижения");
        }
        claim.setCountScoreForIndividualAchievements(individualScore);

        claim.setSummaryOfScore(sumScore);
        if(!rawUserData.get(metaInf.CHAMPION_ID).ownText().trim().matches("\\s*—\\s*"))
            claim.setChampion(true);
        //user.addClaim(claim);
    }

}
