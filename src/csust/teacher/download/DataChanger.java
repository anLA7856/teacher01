package csust.teacher.download;

import java.util.Observable;

/**

 */
public class DataChanger extends Observable {
    private static DataChanger dataChanger;

    public static DataChanger getInstance(){
        if(dataChanger == null){
            dataChanger = new DataChanger();
        }
        return dataChanger;
    }

    public synchronized void notifyDownloadDataChange(){
        setChanged();
        notifyObservers();
    }
}
