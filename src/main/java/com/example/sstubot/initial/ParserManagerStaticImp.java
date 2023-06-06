package com.example.sstubot.initial;

import com.example.sstubot.database.model.urils.ParserManager;
import org.springframework.stereotype.Component;

@Component
public class ParserManagerStaticImp implements ParserManager
{
    @Override
    public boolean isParserWork() {
        return true;
    }

    @Override
    public int getIntervalBetweenParsingInSec() {
        return 1000 * 60; //1 минута
    }
}
