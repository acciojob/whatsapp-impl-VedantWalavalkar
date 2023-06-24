package com.driver;

import java.text.DateFormat;
import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception{
        if(userMobile.contains(mobile)){
            throw new Exception("User already exists");
        }
        User user = new User(name, mobile);
        userMobile.add(mobile);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        if(users.size() == 2){
            Group group = new Group(users.get(1).getName(),2);
            groupUserMap.put(group,users);
            adminMap.put(group, users.get(0));
            return group;
        }
        else{
            String grpName = "Group " + customGroupCount;
            customGroupCount++;
            Group group = new Group(grpName, users.size());
            groupUserMap.put(group,users);
            adminMap.put(group,users.get(0));
            return group;
        }
    }

    public int createMessage(String content) {
        Message newMsg = new Message(messageId,content, new Date(System.currentTimeMillis()));
        messageId++;
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        List<User> userList = groupUserMap.get(group);
        boolean senderPresent = false;
        for(User user:userList){
            if(user == sender) senderPresent = true;
        }
        if(!senderPresent)
        {
            throw new Exception("You are not allowed to send message");
        }
        if(groupMessageMap.containsKey(group)){
            List<Message> messageList = groupMessageMap.get(group);
            messageList.add(message);
            groupMessageMap.put(group,messageList);
            message.setId(messageId);
            message.setTimestamp(new Date(System.currentTimeMillis()));
            messageId++;
            return messageList.size();
        }
        else {
            List<Message> newList = new ArrayList<>();
            newList.add(message);
            groupMessageMap.put(group, newList);
            message.setId(messageId);
            message.setTimestamp(new Date(System.currentTimeMillis()));
            messageId++;
            return 1;
        }
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(!groupUserMap.containsKey(group))
            throw new Exception("Group does not exist");
        if(approver != groupUserMap.get(group).get(0))
            throw new Exception("Approver does not have rights");
        boolean userPresent = false;
        int userIdx = -1;
        List<User> userList = groupUserMap.get(group);
        for(int i = 0 ; i<userList.size() ; i++){
            if(userList.get(i) == user) {
                userPresent = true;
                userIdx = i;
            }
        }
        if(!userPresent){
            throw new Exception("User is not a participant");
        }
        User temp = userList.get(0);
        userList.set(0,userList.get(userIdx));
        userList.set(userIdx,temp);
        return "SUCCESS";
    }
}
