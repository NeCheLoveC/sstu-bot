package com.example.sstubot.initial;

import com.example.sstubot.database.model.Institute;
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
import java.util.Set;

@Component
public class InstitutesLoad
{
    public static String urlListOfDirections = "https://abitur.sstu.ru/vpo/level/2022/b/o";

    protected InstituteService instituteService;

    @Autowired
    public InstitutesLoad(InstituteService instituteService) {
        this.instituteService = instituteService;
        //this.instituteHashMap = new HashMap<>();
    }

    @Transactional
    public HashMap<String, Institute> load()
    {
        HashMap<String, Institute> result = new HashMap<>();
        try {
            Document document = Jsoup.connect(urlListOfDirections).get();
            Elements elements = document.getElementsByClass("table-structure-subtitle");
            elements.remove(elements.size() - 1);
            for(Element element : elements)
            {
                Institute institute = new Institute();
                institute.setName(element.text());
                result.put(institute.getName(),institute);
                //instituteService.save(institute);
            }
        }
        catch (IOException err){
            //отправить в лог
            System.out.println(err);
        }
        return result;
    }
}
