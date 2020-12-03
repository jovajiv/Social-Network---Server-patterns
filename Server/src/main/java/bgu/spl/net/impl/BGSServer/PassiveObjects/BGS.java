package bgu.spl.net.impl.BGSServer.PassiveObjects;

import java.util.concurrent.ConcurrentHashMap;

public class BGS {


    private ConcurrentHashMap<String, User> usernameToData;
    private ConcurrentHashMap<Integer,String> conIdToUsername;

    public BGS(){
        this.usernameToData=new ConcurrentHashMap<>();
        this.conIdToUsername=new ConcurrentHashMap<>();
    }



    public User getUserByName(String username) {
        return usernameToData.get(username);
    }




        //Creates user , if sucessfully added to the BGS DataBase, return true.
    // if failed to add the user to BGS database, return false

    public Boolean Register(String username,String password){
        if (usernameToData.putIfAbsent(username,new User(username,password))==null)           // putIfAbsent returns null if sucesffuly inserted the value, will return previous value mapped to key if key already exists.
            return true;
        else
            return false;
    }



    //return true If the username exists , the password is correct, and the user isn't already logged in
    //return false otherwise

    public boolean attemptLogin(String username,String password,int conId){
        boolean result=false;
        User user=usernameToData.get(username);
            if(user!=null && user.getPassword().equals(password) && conIdToUsername.get(conId)==null  && user.getAndSetConnectionId(conId)){
                conIdToUsername.put(conId,username);
                result=true;
        }
        return result;
    }



    public boolean attemptLogout(int conId){
        boolean result=false;
        String username=conIdToUsername.get(conId);
        if(username!=null) {
            User user = usernameToData.get(username);
            if ( user!=null && user.getAndlogOut()) {
                conIdToUsername.remove(conId);
                result=true;
            }
        }
        return result;
    }

    //return username if he's logged in,
    //return null otherwise.
    public String getUserNameByID(int id){
        return conIdToUsername.get(id);
    }

    public Integer getIdByUsername(String username){
        Integer result=null;
        User user;
        if((user=usernameToData.get(username)) != null)
            result=user.getConnectionId();
        return result;
    }

    public ConcurrentHashMap<String, User> getUsernameToData() {
        return usernameToData;
    }







}
