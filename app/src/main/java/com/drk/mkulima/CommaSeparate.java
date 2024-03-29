package com.drk.mkulima;

import java.text.NumberFormat;
import java.util.Locale;

class CommaSeparate {


    public static String getFormatedNumber(String number){
        if(number!=null) {
            try {
                double val = Double.parseDouble(number);
                return NumberFormat.getNumberInstance(Locale.US).format(val);
            }catch (Exception e){
                return "Error";
            }

        }else{
            return "XX,XXX";
        }
    }
}
