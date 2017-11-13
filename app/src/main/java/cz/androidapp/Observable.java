package cz.androidapp;

/**
 * Created by tomas on 13.11.17.
 */

public interface Observable {
    public void addObserver(Observer o);
    public void removeObserver(Observer o);
}
