package com.example.sstubot.initial;

import com.example.sstubot.database.model.Institute;
import com.example.sstubot.database.service.InstituteService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class InstitutesLoad
{
    public static String urlListOfDirections = "https://abitur.sstu.ru/vpo/level/2022/b/o";

    protected InstituteService instituteService;

    @Autowired
    public InstitutesLoad(InstituteService instituteService) {
        this.instituteService = instituteService;
    }

    public void load()
    {
        try {
            Document document = Jsoup.connect(urlListOfDirections).get();
            Elements elements = document.getElementsByClass("table-structure-subtitle");
            elements.remove(elements.size() - 1);
            for(Element element : elements)
            {
                Institute institute = new Institute();
                institute.setName(element.text());
                instituteService.save(institute);
            }
        }
        catch (IOException err){
            //отправить в лог
            System.out.println(err);
        }
    }
}
