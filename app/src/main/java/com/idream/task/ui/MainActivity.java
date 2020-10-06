package com.idream.task.ui;

import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.idream.task.R;
import com.idream.task.dto.User;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    // using RxJava's Disposable to clear all running threads to avoid Null pointer Exception on Activity onStop()
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this code snippet working in Asynchronous using RxJava or  you can also use WorkManager/Thread/Runnable
        // I am doing it on main Thread because of time constraint.
        usersObservable()
                .subscribeOn(Schedulers.io())   /** This {@link Schedulers} can be used for asynchronously performing blocking IO */
                .observeOn(AndroidSchedulers.mainThread())   /** A {@link Schedulers} which executes actions on the Android main thread. */
                .subscribe(new Observer<List<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(List<User> users) {

                        // GOTO Device Explorer on Right Bottom Android Studio ->>>
                        // Check this Location for Created JSON file /data/user/0/com.idream.task/files/Output.json
                        // we can also move this code snippet(Writing file) to RxObservable
                        File file = new File(getFilesDir(), "Output.json");
                        Gson gson = new GsonBuilder().create();
                        FileWriter fileWriter = null;
                        try {
                            fileWriter = new FileWriter(file);
                            String json = gson.toJson(users);
                            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                            bufferedWriter.write(json);
                            bufferedWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        // FirebaseCrashlytics.getInstance().recordException(e);
                    }

                    @Override
                    public void onComplete() {
                        Timber.i("%s", "<<<<<<<<-----GOTO Device Explorer on Right Bottom Android Studio----->>>>>>>>>>");
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private Observable<List<User>> usersObservable() {

        Observable<List<User>> usersObservable = Observable.create(new ObservableOnSubscribe<List<User>>() {
            @Override
            public void subscribe(ObservableEmitter<List<User>> emitter) throws Exception {
                try {
                    HSSFSheet hssfSheet = openWorkSheetFromAsset();
                    List<User> users = fetchDataFromOpenedExcel(hssfSheet);
                    emitter.onNext(users);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
        return usersObservable;
    }

    private void setData(User user, HSSFCell myCell, int colno) {
        switch (colno) {
            case 0:
                user.setFirst_name(myCell.toString());
                break;
            case 1:
                user.setLast_name(myCell.toString());
                break;
            case 2:
                user.setCompany_name(myCell.toString());
                break;
            case 3:
                user.setAddress(myCell.toString());
                break;
            case 4:
                user.setCity(myCell.toString());
                break;
            case 5:
                user.setCounty(myCell.toString());
                break;
            case 6:
                user.setState(myCell.toString());
                break;
            case 7:
                user.setZip(myCell.toString());
                break;
            case 8:
                user.setPhone(myCell.toString());
                break;
            case 9:
                user.setPhone1(myCell.toString());
                break;
            case 19:
                user.setEmail(myCell.toString());
                break;


        }
    }

    // open Excel from Asset
    private HSSFSheet openWorkSheetFromAsset() throws IOException {
        InputStream myInput;
        // initialize asset manager
        AssetManager assetManager = getAssets();
        //  open excel file name as myexcelsheet.xls
        myInput = assetManager.open("contacts.xls");
        // Create a POI File System object
        POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

        // Create a workbook using the File System
        HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

        // Get the first sheet from workbook
        return myWorkBook.getSheetAt(0);
    }

    private List<User> fetchDataFromOpenedExcel(HSSFSheet hssfSheet) {
        // We now need something to iterate through the cells.
        Iterator<Row> rowIter = hssfSheet.rowIterator();
        int rowno = 0;
        List<User> users = new ArrayList<>();
        while (rowIter.hasNext()) {
            Timber.d(" row no %d", rowno);
            HSSFRow myRow = (HSSFRow) rowIter.next();
            if (rowno != 0) {
                Iterator<Cell> cellIter = myRow.cellIterator();
                int colno = 0;
                String sno = "", date = "", det = "";
                User user = new User();
                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    setData(user, myCell, colno);
                    colno++;
                    Timber.d(" Index : %d  -- %s", myCell.getColumnIndex(), myCell.toString());
                    users.add(user);
                }
                Timber.i(sno + " -- " + date + "  -- " + det + "\n");
            }
            rowno++;
        }
        return users;
    }
}