package com.example.sstubot.initial;

import com.example.sstubot.database.model.*;
import com.example.sstubot.database.model.urils.ClaimType;
import com.example.sstubot.database.service.DirectionService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadManager
{
    private DirectionService directionService;
    private Map<String, User> userMap = new HashMap<>();
    final int USER_CODE_ID = 1;
    final int AMOUNT_SCORE_ID = 2;
    final String URL_ADDRESS_TO_LIST = "https://abitur.sstu.ru";
    public void loadWrapper(List<Direction> directionList)
    {
        try
        {

        }
        catch (Exception err)
        {

        }
    }

    private void load(List<Direction> directionList) throws IOException {
        for(Direction direction : directionList)
        {
            if(direction.getMetaInfo() != null)
                loadUsersIntoDirection(direction);
        }
    }

    //Загрузка уникальных юзеров со страницы списков (как бюджет, так и коммерция)
    private void loadUsersIntoDirection(Direction direction) throws IOException {
        String urlToBudgetClaims = direction.getUrlToListOfClaims();
        MetaInfoAboutUserIntoDirection metaInfo = direction.getMetaInfo();
        String urlToBudgetClaim;
        if(validateURLtoClaims(direction.getUrlToListOfClaims()))
        {
            urlToBudgetClaim = URL_ADDRESS_TO_LIST + direction.getUrlToListOfClaims();
            Document document = Jsoup.connect(urlToBudgetClaim).get();
            Elements divTables = document.select("div.ras-block"); //Каждый блок содержит название таблицы и саму таблицу
            //Elements tbodyOfUsers = document.select("tbody.text-center");
            //Для бюджета
            for(Element divTable : divTables)
            {
                // TODO: 07.05.2023 определить какая это таблица
                //Таблица по целевому,общему и специальному направлению
                Element divTableName = divTable.select("div.block-title div").get(1);
                String tableName = divTableName != null ? divTableName.text().trim() : null ;
                ClaimType claimType = defineClaimType(tableName);
                Element tableTBody = divTable.getElementsByAttribute("tbody").first();
                if(tableTBody == null)
                    continue;
                searchUsersIntoTable(direction, tableTBody,claimType);
            }
        }
        if(validateURLtoClaims(direction.getUrlToListOfClaimsCommerce()))
        {
            Document documentForCommerce = Jsoup.connect(URL_ADDRESS_TO_LIST + direction.getUrlToListOfClaimsCommerce()).get();
            Element divRaspBlock = documentForCommerce.selectFirst("div.rasp-block");
            if(divRaspBlock != null)
            {
                Element divTableName = divRaspBlock.select("div.block-title div").get(1);
                String tableName = divTableName != null ? divTableName.text().trim() : null;
                if(!tableName.matches(".*платной.*"))
                    throw new RemoteException("Не распознано название таблицы : " + tableName);
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

    private void searchUsersIntoTable(Direction direction, Element tbody, ClaimType claimType)
    {
        Elements rawUsersData = tbody.children();
        for(Element userRaw : rawUsersData)
        {
            loadRawUserFromTableString(direction, userRaw.children(),claimType);
        }
    }

    /**
     * @param rawUserData Группа элементов-столбцов, олицетворяющих User's в таблице (дочерние элементы tr  таблице)
     * @return User
     */
    private void loadRawUserFromTableString(Direction currentDirection, Elements rawUserData, ClaimType claimType)
    {
        String userCode = rawUserData.get(MetaInfoAboutUserIntoDirection.USER_CODE_ID).text().trim();
        //User-id найден впервые -> создаем юзера, заявку и список приоретета
        if(!this.userMap.containsKey(userCode))
        {
            Pattern pattern = Pattern.compile("\\s*((подано)|(зачислен))\\s*",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(rawUserData.get(currentDirection.getMetaInfo().CONDITION_ID).text());
            boolean isActualClaim = matcher.matches();
            if(!isActualClaim)
                return;

            User user = user = new User();
            user.setUniqueCode(userCode);

            Elements rawPriorities = rawUserData.get(MetaInfoAboutUserIntoDirection.USER_CODE_ID).getElementsByAttribute("a");
            for(int i = 0;i < rawPriorities.size();i++)
            {
                Element p = rawPriorities.get(i);
                Direction direction = directionService.getDirectionByBudgetUrl(p.attr("href"));
                boolean isBudget = p.attr("title").matches(".*бюджет.*");
                ClaimPriorities priority = new ClaimPriorities(direction, isBudget);
                user.addPriorities(priority);
            }
            formedClaimAndAdd(currentDirection, rawUserData, claimType, user);
            this.userMap.put(userCode,user);
        }
        else
        {
            User user = userMap.get(userCode);
            Pattern pattern = Pattern.compile("\\s*((подано)|(зачислен))\\s*",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(rawUserData.get(currentDirection.getMetaInfo().CONDITION_ID).text());
            boolean isActualClaim = matcher.matches();
            if(!isActualClaim)
                return;
            formedClaimAndAdd(currentDirection, rawUserData, claimType, user);
        }
    }

    private void formedClaimAndAdd(Direction currentDirection, Elements rawUserData, ClaimType claimType, User user) {
        List<Exam> exams = currentDirection.getExams();
        Claim claim = new Claim(user,currentDirection,claimType);
        List<Score> scores = new LinkedList<>();
        MetaInfoAboutUserIntoDirection metaInf = currentDirection.getMetaInfo();
        int id = 0;
        for(int i = MetaInfoAboutUserIntoDirection.AMOUNT_SCORE_ID + 1;i < metaInf.AMOUNT_SCORE_FOR_INDIVIDUAL_ACHIEVEMENTS;i++)
        {
            int points = 0;
            try
            {
                points = Integer.valueOf(rawUserData.get(i).text().trim());
            }
            catch (NumberFormatException err)
            {
                System.out.println("Ошибка преобразования кол-во баллов: " + currentDirection.getUrlToListOfClaims());
            }
            Score score = new Score();
            score.setExam(exams.get(id));
            score.setScore(points);
            score.setClaim(claim);
            scores.add(score);
            id++;
        }
        user.addClaim(claim);
    }

}
