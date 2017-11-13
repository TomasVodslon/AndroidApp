package cz.androidapp;

import java.util.ArrayList;

/**
 * Created by tomas on 13.11.17.
 */

public class ObservableImpl implements Observable {

    ArrayList<Observer> list = new ArrayList<>();



    @Override
    public void addObserver(Observer o) {
        list.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        list.remove(o);
    }

    public void Notify(double degree){

        for (Observer o: list) {
            o.update(degree);
        }
    }
}
