package edu.zju.gis.sdch.ui;

import java.util.ArrayList;

public class ManageCategory {

    // public MyCategory[] allcategory= new MyCategory[];
    public ArrayList<MyCategory> allcategory = new ArrayList<MyCategory>();

    //public ArrayList<String> allallcategorydescribe=new ArrayList<String>();
    public void Addcategory(MyCategory mc) {
        allcategory.add(mc);
    }

    public void Deletecategory(MyCategory mc) {
        allcategory.remove(mc);
    }

    public ArrayList<String> Allallcategorydescribe(ArrayList<MyCategory> allcategory) {
        ArrayList<String> allallcategorydescribe = new ArrayList<String>();
        for (int i = 0; i < allcategory.size(); i++) {
            ((ArrayList<String>) allallcategorydescribe).add(allcategory.get(i).GetCategorydescribtion());
        }
        return allallcategorydescribe;
    }
}
