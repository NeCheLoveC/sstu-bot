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

@Component
public class DirectionLoad
{
    DirectionService directionService;
    InstituteService instituteService;

    @Autowired
    public DirectionLoad(DirectionService directionService, InstituteService instituteService)
    {
        this.directionService = directionService;
        this.instituteService = instituteService;
    }

    @Transactional
    public void load(HashMap<String, Institute> instituteHashMap)
    {
        try
        {
            Document document = Jsoup.connect(InstitutesLoad.urlListOfDirections).get();
            Element table = document.getElementsByTag("tbody").first();

            Elements elements = table.getElementsByTag("tr");
            Institute institute = null;
            Element el;
            for(int i = 0; i < elements.size();i++)
            {
                el = elements.get(i);
                if(isInstitute(el))
                {
                    institute = instituteHashMap.get(el.select("th.table-structure-subtitle").first().text());
                    if(institute != null)
                        instituteService.save(institute);
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
                        Element test = valuesOfDirection.get(1).selectFirst("*");
                        direction.setAbbreviation(valuesOfDirection.get(1).select("nobr").text());
                        direction.setAmountMainBudgetIntoPlan(Integer.valueOf(valuesOfDirection.get(2).text()));
                        direction.setAmountUnusualQuota(Integer.valueOf(valuesOfDirection.get(6).text()));
                        direction.setAmountSpecialQuota(Integer.valueOf(valuesOfDirection.get(8).text()));
                        direction.setAmountTargetQuota(Integer.valueOf(valuesOfDirection.get(10).text()));
                        direction.setAmountBudget(Integer.valueOf(valuesOfDirection.get(12).text()));
                        directionService.save(direction);
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
