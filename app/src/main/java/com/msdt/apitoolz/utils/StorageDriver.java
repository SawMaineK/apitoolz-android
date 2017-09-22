package com.msdt.apitoolz.utils;
import com.msdt.apitoolz.APIToolz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.List;

/**
 * Created by SMK on 3/29/2017.
 */

public class StorageDriver {

    public static String filePath = APIToolz.getInstance().getStoragePath();

    private static StorageDriver instance;

    public StorageDriver(){
        IfExistFileDir();
    }
    public Boolean IfExistFileDir(){
        File fileDir = new File(filePath);
        if(!fileDir.exists()){
            try {
                fileDir.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
        return true;
    }

    public static StorageDriver getInstance() {
        if(instance == null){
            instance = new StorageDriver();
        }
        return instance;
    }

    public <E> List<E> updateTo(String key, Integer position , E value){
        List<E> list = readFile(key);
        if(list != null){
            list.set(position, value);
            return writeFile(key, list);
        }
        return null;
    }

    public <E> List<E> appendTo(String key, E value){
        List<E> list = readFile(key);
        if(list != null){
            list.add(value);
            return writeFile(key, list);
        }
        return null;
    }

    public <E> List<E> appendObject(String key, E value){
        List<E> list = readFile(key);
        if(list != null){
            list.add(value);
            return writeFile(key, list);
        }
        return null;
    }

    public <E> List<E> appendList(String key, Collection<? extends E> value){
        List<E> list = readFile(key);
        if(list != null){
            list.addAll(value);
            return writeFile(key, list);
        }
        return null;
    }

    public <E> List<E> deleteFrom(String key, int positon){
        List<E> list = readFile(key);
        if(list != null){
            list.remove(positon);
            return writeFile(key, list);
        }
        return null;
    }

    public <E> List<E> deleteFrom(String key, E value){
        List<E> list = readFile(key);
        if(list != null){
            list.remove(value);
            return writeFile(key, list);
        }
        return null;
    }

    public <E> E saveTo(String key, E value){
        return writeFile(key, value);
    }

    public <E> E selectFrom(String key){
        return readFile(key);
    }

    public boolean destroy(String key){
        String myFile = filePath + "/" + key +".ser";
        boolean ret = false;
        File file = new File("", myFile);
        if (file.exists()) {
            file.delete();
            ret = true;
        }
        return ret;
    }



    private <E> E writeFile(String fileName, E obj){
        String myFile = filePath + "/" + fileName +".ser";
        try
        {
            FileOutputStream fileOut = new FileOutputStream(myFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(obj);
            out.close();
            fileOut.close();
            return obj;
        } catch (NotSerializableException e){
            e.printStackTrace();
            return null;
        } catch(IOException i)
        {
            i.printStackTrace();
            return null;
        }catch (OutOfMemoryError o){
            destroy(fileName);
            o.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <E> E readFile(String fileName){
        String myFile = filePath + "/" + fileName +".ser";
        E t = null;
        if((new File(myFile).exists())){
            try
            {
                FileInputStream fileIn = new FileInputStream(myFile);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                t = (E) in.readObject();
                in.close();
                fileIn.close();
            }catch(IOException i)
            {
                i.printStackTrace();
            }catch(ClassNotFoundException c)
            {
                c.printStackTrace();
            }catch (OutOfMemoryError o){
                destroy(fileName);
                o.printStackTrace();
            }
        }
        return t;
    }
}
