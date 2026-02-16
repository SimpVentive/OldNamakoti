package com.namakoti.beans;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by anusha on 2/11/2018.
 */

public class TempleBean extends ErrorBean {

    @SerializedName("temples")
    public ArrayList<Temples> temples;

    @SerializedName("print_type")
    public ArrayList<PrintType> print_type;

    @SerializedName("count_number")
    public ArrayList<CountNumber> count_number;

    public class Temples{
        @SerializedName("temple_id")
        public String temple_id;

        @SerializedName("temple_name")
        public String temple_name;
    }

    public class PrintType{
        @SerializedName("printing_id")
        public String printing_id;

        @SerializedName("prinitng_name")
        public String prinitng_name;
    }
    public class CountNumber{
        @SerializedName("id")
        public String id;

        @SerializedName("no_count")
        public String no_count;
    }
}
