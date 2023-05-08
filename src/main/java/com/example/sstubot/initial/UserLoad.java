package com.example.sstubot.initial;

import com.example.sstubot.database.model.*;
import com.example.sstubot.database.model.urils.ClaimType;
import com.example.sstubot.database.service.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class UserLoad
{
    protected ExamService examService;
    protected DirectionService directionService;
    protected ClaimService claimService;
    protected ScoreService scoreService;
    protected UserService userService;
    final int USER_CODE_ID = 1;
    final int AMOUNT_SCORE_ID = 2;
    final String URL_ADDRESS_TO_LIST = "https://abitur.sstu.ru/";

    @Autowired
    public UserLoad(ExamService examService, DirectionService directionService, ClaimService claimService, ScoreService scoreService, UserService userService) {
        this.examService = examService;
        this.directionService = directionService;
        this.claimService = claimService;
        this.scoreService = scoreService;
        this.userService = userService;
    }

    public List<Claim> getAllClaimsByDirection(List<Direction> directions)
    {
        try{
            for(Direction dir : directions)
            {

                Document document = Jsoup.connect(dir.getUrlToListOfClaims()).get();
                //Получить экзамены по направлению
                Element tableHead = document.selectFirst("thead.text-center");
                List<Exam> exams = getExamsInDirection(tableHead);


                final int AMOUNT_SCORE_FOR_INDIVIDUAL_ACHIEVEMENTS = AMOUNT_SCORE_ID + exams.size() + 1;
                final int DOCUMENT_TYPE_ID = AMOUNT_SCORE_FOR_INDIVIDUAL_ACHIEVEMENTS + 1;
                final int AGREEMENT_ID = DOCUMENT_TYPE_ID + 1;
                final int CONDITION_ID = AGREEMENT_ID + 2;//Состояние - подано/отозвано/зачислен
                final int CHAMPION_ID = CONDITION_ID + 2;
                        //Заявки на направление
                Element usersIntoTBody = document.getElementsByTag("tbody").first();
                Elements usersAsTr = usersIntoTBody.children();
                for(Element userRaw : usersAsTr)
                {
                    Elements usersData = userRaw.children();

                    if(!usersData.get(CONDITION_ID).text().matches("((Подано)|(Зачислен))"))
                    {
                        continue;
                    }

                    String userId = usersData.get(USER_CODE_ID).text().trim();
                    User user = userService.getUserByUniqueCode(userId);
                    Element priority = usersData.get(USER_CODE_ID).selectFirst("div.small");
                    if(priority == null)
                    {
                        System.out.println("Приоритетность не обнаружена");
                        continue;
                    }
                    Elements pr = priority.select("a");
                    List<Claim> claimList = new LinkedList<>();
                    //p - ссылка (<a>) на приоритетность направления у АБИТУРИЕНТА (User)
                    for(Element p : pr)
                    {
                        String url = p.attr("href");
                        Claim c = new Claim();
                        Direction direction = directionService.getDirectionByBudgetUrl(url);
                        if(direction == null)
                            throw new RuntimeException("Direction не найден");
                        c.setDirection(direction);
                        c.setUser();
                    }
                    User user = new User();
                    user.setUniqueCode(userId);
                    Claim claim = new Claim();

                }
            }
        }
        catch (Exception err)
        {
            System.out.println(err.getMessage());
            System.out.println(err.getStackTrace());
        }
    }

    public List<Exam> getExamsInDirection(Element element)
    {
        List<Exam> exams = new LinkedList<>();
        Elements elements = element.children();
        for (int i = AMOUNT_SCORE_ID + 1;true; i++)
        {
            Element part = elements.get(i);
            String textInsideElement = part.text();
            if(textInsideElement.matches(".*за инд. дост.*"))
            {
                break;
            }
            Exam exam = new Exam(textInsideElement);
            exams.add(exam);
        }
        if(exams.isEmpty())
            throw new RuntimeException("Дисциплина не содержит вступительных экзаменов");
        return exams;
    }


    public User getUserFromElement(Element element)
    {
        final int ID = 1;

        LinkedList<Exam> score = new LinkedList<>();
        Elements partOfUser = element.children();
        //Получаем баллы по экзаменам
        for(int i = AMOUNT_SCORE + 1;true;i++)
        {
            Element exam = partOfUser.get(i);
        }
    }

    public List<User> getAllUser(List<Direction> directions)
    {

    }

    private List<Claim> getClaimsIntoElementBlock(Element element, ClaimType claimType)
    {

    }

    @Transactional
    public void loadBudget(String urlToDirection, Direction direction)
    {
        try
        {
            Document document = Jsoup.connect(urlToDirection).get();
            Elements groupsOfBudget = document.select("div.rasp-block");

            Element el = groupsOfBudget.first();

            Element infoAboutGroup = el.selectFirst("div.mb-1").nextElementSibling();
            String textInfo = infoAboutGroup.text();


            Element tableOfClaims = el.getElementsByTag("table").first();
            Element headOfTable = tableOfClaims.selectFirst("thead");
            Element stringOfHead = headOfTable.getElementsByTag("tr").first();

            Elements parametrs = stringOfHead.select("th");
            //Iterator<Element> iter = parametrs.iterator();
            List<Exam> exams = new LinkedList<>();
            int a = 3;//Пезоция первого экзамена (смещение)
            for(int i = 0;true;i++)
            {
                Element elem = parametrs.get(a + i);
                String nameExam = elem.text();
                if(nameExam.startsWith("Сумма"))
                    break;
                Exam exam = new Exam();
                exam.setName(nameExam);

                if(!examService.existExam(exam.getName()))
                    examService.save(exam);
                else
                {
                    exam = examService.getExamByName(exam.getName());
                }
                exams.add(exam);
            }
            for(Element elementOfGroup : groupsOfBudget)
            {
                if(textInfo.matches(".*\bцелевое\b.*"))
                {
                    //В целевое
                    Element tBodyTable = elementOfGroup.selectFirst("tbody");
                    getAllUsersByUrlFromTable(tBodyTable, direction, direction.getUrlToListOfClaims(),exams, ClaimType.BUDGET);


                    //if(direction.getUrlToListOfClaimsCommerce() != null && !direction.getUrlToListOfClaimsCommerce().isEmpty() && !direction.getUrlToListOfClaimsCommerce().isBlank())
                        //getAllUsersByUrlFromTable(tBodyTable, direction, direction.getUrlToListOfClaimsCommerce(),exams, DirectionType.COMMERCE);
                }
                else if(textInfo.matches(".*специальн.*"))
                {
                    //Спец квота
                    Element tBodyTable = elementOfGroup.selectFirst("tbody");
                    getAllUsersByUrlFromTable(tBodyTable, direction, direction.getUrlToListOfClaims(),exams, ClaimType.BUDGET);
                }
                else if(textInfo.matches(".*особ.*"))
                {
                    //Особые права
                    Element tBodyTable = elementOfGroup.selectFirst("tbody");
                    getAllUsersByUrlFromTable(tBodyTable, direction, direction.getUrlToListOfClaims(),exams, ClaimType.BUDGET);
                }
                else if(textInfo.matches(".*общ.*"))
                {
                    //Общий конкурс
                    Element tBodyTable = elementOfGroup.selectFirst("tbody");
                    getAllUsersByUrlFromTable(tBodyTable, direction, direction.getUrlToListOfClaims(),exams, ClaimType.BUDGET);
                }


            }
        }
        catch (IOException err)
        {
            System.out.println(err.getMessage());
        }
    }

    //Element - tbody
    private List<User> getAllUsersByUrlFromTable(Element tBodytable, Direction direction, String url,List<Exam> exams, ClaimType directionType)
    {
        List<User> userList = new LinkedList<>();
        try
        {
            //Document document = Jsoup.connect(url).get();
            Elements users = tBodytable.select("tr");
            for(Element userClaim : users)
            {
                User userEntity = new User();
                //Claim claim = new Claim();
                //claim.setUserId();
                Elements propertyOfUser = userClaim.select("td");
                String uniqueCode = propertyOfUser.get(1).text();
                userEntity.setUniqueCode(uniqueCode);
                List<Score> scores = new LinkedList<>();
                int delta = 3;
                boolean isIgnore = false;
                for (int i = 0;i < exams.size() && !isIgnore;i++)
                {
                    Score score = new Score();
                    score.setExam(exams.get(i));
                    try
                    {
                        score.setScore(Integer.valueOf(propertyOfUser.get(delta + i).text()));
                    }
                    catch (NumberFormatException err)
                    {
                        if(propertyOfUser.get(delta + i).text().equals("Неявка"))
                        {
                            isIgnore = true;
                            score.setScore(null);
                            break;
                        }
                        else
                        {
                            score.setScore(0);
                        }
                    }
                    scores.add(score);
                }
                if(isIgnore)
                    continue;
                if(!userService.userExist(userEntity.getUniqueCode()))
                {
                    userService.save(userEntity);
                }
                else
                {
                    userService.getUserByUniqueCode(userEntity.getUniqueCode());
                }
                Claim claim = new Claim(userEntity,direction);


                claim.setCountScoreForIndividualAchievements(Integer.valueOf(propertyOfUser.get(delta + exams.size()).text()));
                String champion = propertyOfUser.last().text();
                claim.setChampion(champion == null || champion.isEmpty() || champion.isBlank());

                //Копия / Оригинал
                String typeOfClaim = propertyOfUser.get(delta + exams.size() + 1).text();

                if(typeOfClaim.equals("Копия"))
                    continue;

                claim.addScore(scores);
                userList.add(userEntity);
                claimService.save(claim);
            }

        }
        catch (Exception err)
        {
            System.out.println(err.getMessage());
        }
        return userList;
    }
}
