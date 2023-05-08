package com.example.sstubot.initial;

import com.example.sstubot.database.model.Direction;
import com.example.sstubot.database.model.Institute;
import com.example.sstubot.database.service.DirectionService;
import com.example.sstubot.database.service.InstituteService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Component
public class DirectionLoad
{
    DirectionService directionService;
    InstituteService instituteService;

    protected final String domainUrl = "https://abitur.sstu.ru/";
    @Autowired
    public DirectionLoad(DirectionService directionService, InstituteService instituteService)
    {
        this.directionService = directionService;
        this.instituteService = instituteService;
    }

    @Transactional
    public List<Direction> load(HashMap<String,Institute> institutes)
    {
        List<Direction> listOfDirections = new LinkedList<>();
        try
        {
            Document document = Jsoup.connect(InstitutesLoad.urlListOfDirections).get();
            Element table = document.getElementsByTag("tbody").first();
            Elements elements = table.getElementsByTag("tr");
            Institute institute = null;
            Element el = null;
            for(int i = 0; i < elements.size();i++)
            {
                el = elements.get(i);
                if(isInstitute(el))
                {
                    institute = institutes.get(el.select("th.table-structure-subtitle").first().text());
                    if(institute != null)
                    {
                        instituteService.save(institute);
                    }
                }
                else
                {
                    if(institute != null)
                    {
                        if(el.children().get(0).text().startsWith("Всего"))
                            continue;
                        Direction direction = new Direction();
                        Elements valuesOfDirection = el.children();
                        direction.setName(valuesOfDirection.get(0).text());
                        direction.setInstitute(institute);
                        //Element test = valuesOfDirection.get(1).selectFirst("*");
                        direction.setAbbreviation(valuesOfDirection.get(1).select("nobr").text());
                        direction.setAmountMainBudgetIntoPlan(Integer.valueOf(valuesOfDirection.get(2).text()));
                        direction.setAmountUnusualQuota(Integer.valueOf(valuesOfDirection.get(6).text()));
                        direction.setAmountSpecialQuota(Integer.valueOf(valuesOfDirection.get(8).text()));
                        direction.setAmountTargetQuota(Integer.valueOf(valuesOfDirection.get(10).text()));
                        direction.setAmountBudget(Integer.valueOf(valuesOfDirection.get(12).text()));
                        direction.setUrlToListOfClaims(domainUrl + valuesOfDirection.get(14).selectFirst("a").attr("href"));
                        direction.setUrlToListOfClaimsCommerce(domainUrl + valuesOfDirection.get(17).selectFirst("a").attr("href"));
                        directionService.save(direction);
                        listOfDirections.add(direction);
                        //institute.addDirection(direction);
                    }

                }

            }
        }
        catch (IOException err)
        {
            //Добавляем ошибку в лог
            System.out.println(err.getMessage());
        }
        return listOfDirections;
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
