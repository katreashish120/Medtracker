package sg.edu.nus.iss.se.ft05.medipal;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import sg.edu.nus.iss.se.ft05.medipal.dao.ICEContactsDAO;
import sg.edu.nus.iss.se.ft05.medipal.dao.ICEContactsDAOImpl;

/**
 * Created by ashish Katre
 */

public class Contacts {

    private static ICEContactsDAO iceContactsDAO;

    private Integer id;
    private Integer priority;
    private Long phone;
    private String name;
    private String description;
    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Contacts(String name, String description, Long phone, String type, Context context) {

        this.name = name;
        this.description = description;
        this.phone = phone;
        this.type = type;
        this.priority = getNewPriority(context);
    }

    public Contacts() {

    }

    public int getNewPriority(Context context) {


        iceContactsDAO = new ICEContactsDAOImpl(context);
        int priority = iceContactsDAO.findMaxPriority();

        Toast.makeText(context, new String("priority :" + priority), Toast.LENGTH_SHORT).show();


        return priority++;
    }

    public static Cursor findAll(Context context) {

        iceContactsDAO = new ICEContactsDAOImpl(context);

        return iceContactsDAO.findAll();
    }

    public static Contacts findById(Context context, long id) {

        iceContactsDAO = new ICEContactsDAOImpl(context);

        return iceContactsDAO.findById(id);
    }

    public long save(Context context) {

        iceContactsDAO = new ICEContactsDAOImpl(context);
        return iceContactsDAO.insert(this);
    }

    public long update(Context context) {

        iceContactsDAO = new ICEContactsDAOImpl(context);

        return iceContactsDAO.update(this);
    }

    public int delete(Context context) {

        iceContactsDAO = new ICEContactsDAOImpl(context);
        return iceContactsDAO.delete(this.getId());
        // TODO Priority
    }

    public int updatePriority(Context context, int priority) {

        iceContactsDAO = new ICEContactsDAOImpl(context);

        this.priority = priority;

        return iceContactsDAO.updatePriority(this);

        // TODO Priority
    }
}
