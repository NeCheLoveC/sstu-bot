package com.example.sstubot.initial;

import com.example.sstubot.database.model.*;
import com.example.sstubot.database.model.urils.DirectionType;
import com.example.sstubot.database.repositories.ExamRepository;
import com.example.sstubot.database.service.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Iterator;
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

    @Autowired
    public UserLoad(ExamService examService, DirectionService directionService, ClaimService claimService, ScoreService scoreService, UserService userService) {
        this.examService = examService;
        this.directionService = directionService;
        this.claimService = claimService;
        this.scoreService = scoreService;
        this.userService = userService;
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
                    getAllUsersByUrlFromTable(tBodyTable, direction, direction.getUrlToListOfClaims(),exams, DirectionType.BUDGET);


                    //if(direction.getUrlToListOfClaimsCommerce() != null && !direction.getUrlToListOfClaimsCommerce().isEmpty() && !direction.getUrlToListOfClaimsCommerce().isBlank())
                        //getAllUsersByUrlFromTable(tBodyTable, direction, direction.getUrlToListOfClaimsCommerce(),exams, DirectionType.COMMERCE);
                }
                else if(textInfo.matches(".*специальн.*"))
                {
                    //Спец квота
                    Element tBodyTable = elementOfGroup.selectFirst("tbody");
                    getAllUsersByUrlFromTable(tBodyTable, direction, direction.getUrlToListOfClaims(),exams, DirectionType.BUDGET);
                }
                else if(textInfo.matches(".*особ.*"))
                {
                    //Особые права
                    Element tBodyTable = elementOfGroup.selectFirst("tbody");
                    getAllUsersByUrlFromTable(tBodyTable, direction, direction.getUrlToListOfClaims(),exams, DirectionType.BUDGET);
                }
                else if(textInfo.matches(".*общ.*"))
                {
                    //Общий конкурс
                    Element tBodyTable = elementOfGroup.selectFirst("tbody");
                    getAllUsersByUrlFromTable(tBodyTable, direction, direction.getUrlToListOfClaims(),exams, DirectionType.BUDGET);
                }


            }
        }
        catch (IOException err)
        {
            System.out.println(err.getMessage());
        }
    }

    //Element - tbody
    private List<User> getAllUsersByUrlFromTable(Element tBodytable, Direction direction, String url,List<Exam> exams, DirectionType directionType)
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
