package bgu.spl.net.impl.BGSServer.PassiveObjects;

import bgu.spl.net.impl.BGSServer.Opcodes.ServerSide.NOTIFICATION;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {
    private String username;
    private String password;
    private ConcurrentLinkedQueue<String> followers;
    private ConcurrentLinkedQueue<String> following;
    private Integer connectionId;
    private ConcurrentLinkedQueue<NOTIFICATION> pendingNotifications;
    private Vector<String> posts; //public messages
    private Vector<String> PMs;     //private messages


    public User(String username,String password) {
        this.username = username;
        this.password = password;
        this.followers = new ConcurrentLinkedQueue<>();
        this.following = new ConcurrentLinkedQueue<>();
        this.connectionId = -1;                                         //if connectionId is -1 , then user is not connected.
        this.pendingNotifications = new ConcurrentLinkedQueue<>();
        posts = new Vector<>();
        PMs = new Vector<>();
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ConcurrentLinkedQueue<String> getFollowers() {
        return followers;
    }


    public ConcurrentLinkedQueue<String> getFollowing() {
        return following;
    }

    public Boolean getUserLoggedIn(){
        return connectionId>=0;
    }

    public ConcurrentLinkedQueue<NOTIFICATION> getPendingNotifications() {
        return pendingNotifications;
    }


    public int getNumOfPosts() {
        return posts.size();
    }


    public Vector<String> getPMs() {
        return PMs;
    }

    public Vector<String> getPosts() {
        return posts;
    }

    public void addFollower(String follower){
        followers.add(follower);
    }

    public void removeFollower(String follower){ followers.remove(follower);}

    public void addFollowing(String whoToFollow){
        this.following.add(whoToFollow);
    }

    public void removeFollowing(String whoToFollow){
        this.following.remove(whoToFollow);
    }

    //Should be called only if user is loggedOff
    public void addNotification(NOTIFICATION notification){
        pendingNotifications.add(notification);
    }



    public void addPMs(String PM) {
        this.PMs.add(PM);
    }

    public void addPosts(String post) {
        this.posts.add(post);
    }

//    public void setConnectionId(int value){
//        this.connectionId=value;
//    }

    //return false if user is already logged in to begin with,
    // return true if the user sucessfully loged in and managed to update the connection id.
    public boolean getAndSetConnectionId(int value){
        synchronized (this.connectionId){
            if(getConnectionId()>=0)
                return false;
            else {
                connectionId = value;
                return true;
            }
        }
    }


    //return false if user isn't  logged in to begin with,
    //return true if user was logged in , and sucessfully changed state to "logged out".
    public boolean getAndlogOut(){
        synchronized (this.connectionId){
            if(getConnectionId()>=0) {
                connectionId = -1;
                return true;
            }
            else {
                return false;
            }
        }
    }

    public Integer getConnectionId() {
        return connectionId;
    }

}
