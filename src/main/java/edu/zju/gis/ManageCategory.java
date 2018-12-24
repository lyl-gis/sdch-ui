package edu.zju.gis;

import java.util.ArrayList;
import java.util.Collection;

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

    public Collection<String> Allallcategorydescribe(ArrayList<MyCategory> allcategory) {
        Collection<String> allallcategorydescribe = new ArrayList<String>();
        for (int i = 0; i < allcategory.size(); i++) {
            ((ArrayList<String>) allallcategorydescribe).add(allcategory.get(i).GetCategorydescribtion());
        }
        return allallcategorydescribe;
    }
}
