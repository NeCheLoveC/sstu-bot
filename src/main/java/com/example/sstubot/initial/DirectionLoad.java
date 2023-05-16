package com.example.sstubot.initial;

import com.example.sstubot.database.model.Direction;
import com.example.sstubot.database.model.Exam;
import com.example.sstubot.database.model.Institute;
import com.example.sstubot.database.model.urils.EducationType;
import com.example.sstubot.database.service.DirectionService;
import com.example.sstubot.database.service.ExamService;
import com.example.sstubot.database.service.InstituteService;
import jakarta.annotation.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.print.Doc;
import java.beans.Transient;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Component
public class DirectionLoad
{
    DirectionService directionService;
    InstituteService instituteService;
    ExamService examService;
    public static String urlListOfDirectionsOCHNAY = "https://abitur.sstu.ru/vpo/level/2022/b/o"; //ОЧКА
    public static String urlListOfDirectionsZAOCHNAY = "https://abitur.sstu.ru/vpo/level/2022/b/z";
    public static String getUrlListOfDirectionsOCHNO_ZAOCHNAY = "https://abitur.sstu.ru/vpo/level/2022/b/oz";
    protected final String domainUrl = "https://abitur.sstu.ru";
    HashMap<String,Institute> institutes = new HashMap<>();
    @Autowired
    public DirectionLoad(DirectionService directionService, InstituteService instituteService, ExamService examService)
    {
        this.directionService = directionService;
        this.instituteService = instituteService;
        this.examService = examService;
    }
    @Transactional
    public List<Direction> load()
    {
        List<Direction> listOfDirections = new LinkedList<>();
        try
        {
            List<Direction> directionOCHNAY = loadDirections(urlListOfDirectionsOCHNAY, EducationType.OChNAYa);//Очные направления
            List<Direction> directionZAOCHNAYA = loadDirections(urlListOfDirectionsZAOCHNAY, EducationType.ZAOChNAYa);//Заочные направления
            List<Direction> directionOCHNO_ZAOCHNAY = loadDirections(getUrlListOfDirectionsOCHNO_ZAOCHNAY, EducationType.OChNO_ZAOChNAYa);//Очно-заочные направления

            listOfDirections.addAll(directionOCHNAY);
            listOfDirections.addAll(directionZAOCHNAYA);
            listOfDirections.addAll(directionOCHNO_ZAOCHNAY);
        }
        catch (IOException err)
        {
            //Добавляем ошибку в лог
            System.out.println(err.getMessage());
        }

        return listOfDirections;
    }


    public List<Direction> loadDirections(String url, EducationType educationType) throws IOException {
        List<Direction> resultList = new LinkedList<>();
        Document document = Jsoup.connect(url).get();
        Element table = document.getElementsByTag("tbody").first();
        Elements elements = table.getElementsByTag("tr");
        Institute institute = null;
        Element el = null;
        for(int i = 0; i < elements.size();i++)
        {
            el = elements.get(i); //el == tr - вся строка
            if(el.firstElementChild().ownText().trim().startsWith("Всего"))
                continue;
            if(isInstitute(el))
            {
                //institute = institutes.get();
                String instituteName = el.firstElementChild().ownText();
                Institute searchingInstitute = instituteService.getInstituteByName(instituteName);
                if(searchingInstitute == null)
                {
                    institute = new Institute(instituteName);
                    instituteService.save(institute);
                }
                else
                {
                    institute = searchingInstitute;
                }
            }
            else
            {
                if(institute != null)
                {
                    Direction direction = new Direction();
                    Elements valuesOfDirection = el.children();
                    if(valuesOfDirection.size() < 18)
                        throw new RuntimeException("Направление содержит меньше 18 ячеек о СЕБЕ     " + valuesOfDirection.get(0).text());
                    direction.setName(valuesOfDirection.get(0).text());
                    direction.setInstitute(institute);
                    direction.setEducationType(educationType);
                    //Element test = valuesOfDirection.get(1).selectFirst("*");
                    direction.setAbbreviation(valuesOfDirection.get(1).select("nobr").text());
                    direction.setAmountMainBudgetIntoPlan(Integer.valueOf(valuesOfDirection.get(2).text()));
                    direction.setAmountUnusualQuota(Integer.valueOf(valuesOfDirection.get(6).text()));
                    direction.setAmountSpecialQuota(Integer.valueOf(valuesOfDirection.get(8).text()));
                    direction.setAmountTargetQuota(Integer.valueOf(valuesOfDirection.get(10).text()));
                    direction.setAmountBudget(Integer.valueOf(valuesOfDirection.get(12).text()));
                    direction.setUrlToListOfClaims(domainUrl + valuesOfDirection.get(14).selectFirst("a").attr("href"));
                    direction.setUrlToListOfClaimsCommerce(domainUrl + valuesOfDirection.get(17).selectFirst("a").attr("href"));


                    MetaInfoAboutUserIntoDirection metaInfo = formedMetaInfoByDirection(direction);
                    //List<Exam> exams = getExams(direction);
                    //direction.setExams(exams);
                    //examService.save(exams);
                    if(metaInfo == null)
                        direction.setIgnoreDirection(true);
                    direction.setMetaInfo(metaInfo);
                    //institute.addDirection(direction);
                    directionService.save(direction);
                    resultList.add(direction);
                }

            }
        }
        return resultList;
    }
    //Попытаться определить набор Экзаменов (Или в бюджете или в коммерции)
    public List<Exam> getExams(Direction direction) throws IOException {
        List<Document> documents = new LinkedList<>();
        if(validateUrl(direction.getUrlToListOfClaims()))
        {
            documents.add(Jsoup.connect(direction.getUrlToListOfClaims()).get());
        }
        if(validateUrl(direction.getUrlToListOfClaimsCommerce()))
        {
            documents.add(Jsoup.connect(direction.getUrlToListOfClaimsCommerce()).get());
        }
        if(documents.isEmpty())
            direction.setIgnoreDirection(true);
        List<Exam> exams = new LinkedList<>();
        for(Document doc : documents)
        {
            List<Exam> buf = tryGetExams(doc);
            if(!buf.isEmpty())
            {
                exams = buf;
                break;
            }
        }
        if(exams.isEmpty())
            direction.setIgnoreDirection(true);
        return exams;
    }
    private List<Exam> tryGetExams(Document document) throws IOException {
        List<Exam> exams = new LinkedList<>();
        Element thead = document.selectFirst("table thead");
        if(thead == null)
            return exams;
        Element th = thead.firstElementChild();
        if(th == null)
            return exams;
        Elements dataOfHeader = th.children();
        for (int i = MetaInfoAboutUserIntoDirection.AMOUNT_SCORE_ID + 1; i < dataOfHeader.size(); i++)
        {
            Element el = dataOfHeader.get(i);
            String textElement = el.text();
            if(textElement.matches(".*Сумма.*"))
            {
                break;
            }
            else
            {
                Exam exam = new Exam(textElement.trim());
                exams.add(exam);
            }
        }
        return exams;
    }
    @Nullable
    private MetaInfoAboutUserIntoDirection formedMetaInfoByDirection(Direction direction) throws IOException {
        String urlBudget = direction.getUrlToListOfClaims();
        String urlCommerce = direction.getUrlToListOfClaimsCommerce();
        MetaInfoAboutUserIntoDirection metaInfo = null;
        //Проверка есть ли заголовк таблицы
        //Проверка есть ли tbody
        //boolean budgetIsExist = false;
        //boolean commerceIsExist = false;


        if(validateUrl(urlBudget))
        {
            Document document = Jsoup.connect(urlBudget).get();
            Element dataOfHead = document.selectFirst("div.rasp-block thead tr");
            if(dataOfHead != null)
            {
                metaInfo = formedMetaInfo(direction,dataOfHead);
                //budgetIsExist = true;
            }
        }
        if(metaInfo == null && validateUrl(urlCommerce))
        {
            Document document = Jsoup.connect(urlCommerce).get();
            Element dataOfHead = document.selectFirst("div.rasp-block thead tr");
            if(dataOfHead != null)
            {
                metaInfo = formedMetaInfo(direction,dataOfHead);
                //commerceIsExist = true;
            }
        }

        if(metaInfo == null)
            System.out.println(direction.getName() + " не имеет структуры (" + direction.getEducationType().name() + ")! Оно будет пропущено при сканировании списков. Метод - formedMetaInfoByDirection");
        return metaInfo;
    }
    @Nullable
    private MetaInfoAboutUserIntoDirection formedMetaInfo(Direction direction,Element tr)
    {
        if(tr == null)
            return null;
        MetaInfoAboutUserIntoDirection metaInfo = null;
        int countOfExam = 0;
        Elements listDataOfHead = tr.children();
        if(listDataOfHead.isEmpty())
            return null;
        List<Exam> exams = new LinkedList<>();
        for (int i = MetaInfoAboutUserIntoDirection.AMOUNT_SCORE_ID + 1;i < listDataOfHead.size();i++)
        {
            Element element = listDataOfHead.get(i);
            if(element.ownText().trim().startsWith("Сумма"))
            {
                break;
            }
            else
            {
                //Сформировать новый экзамен
                Exam exam = new Exam();
                String examName = element.ownText();
                exam.setName(examName);
                exams.add(exam);
            }
            countOfExam++;
        }
        if(countOfExam == 0 || exams.isEmpty())
        {
            System.out.println("Данное направление " + direction.getName() + " " + direction.getEducationType().name() + "не имеет экзаменов???");
            return null;
        }
        else
        {
            return metaInfo = new MetaInfoAboutUserIntoDirection(direction,countOfExam,exams);
        }
    }

    private boolean validateUrl(String url)
    {
        if(url != null && !url.isBlank() && !url.isEmpty())
            return true;
        return false;
    }
    private boolean isInstitute(Element element)
    {
        if(element == null)
            throw new IllegalArgumentException("Element не может быть равен null!");
        if(element.select("th.table-structure-subtitle").size() == 1)
            return true;
        return false;
    }
}
