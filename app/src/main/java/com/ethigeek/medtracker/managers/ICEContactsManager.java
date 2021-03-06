package com.ethigeek.medtracker.managers;

import android.content.Context;
import android.database.Cursor;

import com.ethigeek.medtracker.daoutils.DBHelper;
import com.ethigeek.medtracker.dao.ICEContactsDAO;
import com.ethigeek.medtracker.dao.ICEContactsDAOImpl;
import com.ethigeek.medtracker.domain.ICEContact;

/**
 * Created by ashish Katre
 */

public class ICEContactsManager {

    //Dao
    private ICEContactsDAO iceContactsDAO;

    private ICEContact iceContact;

    /**
     * Getter for ICEContact
     *
     * @return ICEContact
     */
    public ICEContact getICEContact() {

        return iceContact;
    }

    /**
     * Setter for ICEContact
     *
     * @param iceContact
     */
    public void setICEContact(ICEContact iceContact) {

        this.iceContact = iceContact;
    }

    /**
     * Constructor of iceContact
     *
     * @param newICEContact
     */
    public ICEContactsManager(ICEContact newICEContact, Context context) {

        this.iceContact = newICEContact;
        this.iceContact.setPriority(getNewPriority(context));
    }

    /**
     * Default Constructor
     */
    public ICEContactsManager() {

    }

    /**
     * New Priority
     *
     * @param context
     * @return int
     */
    public int getNewPriority(Context context) {

        int newPriority = this.getMaxPriority(context);

        newPriority++;

        return newPriority;
    }

    /**
     * Static method for finding all contacts
     *
     * @param context
     * @return Cursor
     */
    public static Cursor findAll(Context context) {

        ICEContactsDAO iceContactsDAO = new ICEContactsDAOImpl(context);

        return iceContactsDAO.findAll();
    }

    /**
     * Static method for finding all contacts
     *
     * @param context
     * @return Cursor
     */
    public static long findPhone(Context context) {

        long phone = 0;

        ICEContactsDAO iceContactsDAO = new ICEContactsDAOImpl(context);

        Cursor cursor = iceContactsDAO.findAll();

        if (null != cursor && !cursor.isBeforeFirst()) {

            phone = cursor.getLong(cursor.getColumnIndex(DBHelper.ICE_CONTACTS_KEY_PHONE));
        }

        return phone;
    }

    /**
     * Method to find contact by id
     *
     * @param context
     * @param id
     * @return ICEContactsManager
     */
    public ICEContact findById(Context context, int id) {

        iceContactsDAO = new ICEContactsDAOImpl(context);
        this.iceContact = iceContactsDAO.findById(id);

        return iceContact;
    }

    /**
     * Insert contact
     *
     * @param context
     * @return long
     */
    public long save(Context context) {

        iceContactsDAO = new ICEContactsDAOImpl(context);
        return iceContactsDAO.insert(this.iceContact);
    }

    /**
     * update contact
     *
     * @param context
     * @return long
     */
    public long update(Context context) {

        iceContactsDAO = new ICEContactsDAOImpl(context);

        return iceContactsDAO.update(this.iceContact);
    }

    /**
     * delete contacts
     *
     * @param context
     * @return int
     */
    public int delete(Context context) {

        iceContactsDAO = new ICEContactsDAOImpl(context);
        return iceContactsDAO.delete(this.iceContact.getId());
        // TODO Priority
    }

    /**
     * Update priority after delete
     *
     * @param context
     */
    public void updatePriority(Context context) {

        iceContactsDAO = new ICEContactsDAOImpl(context);

        int maxPriority = this.getNewPriority(context);

        for (int currentPriority = this.iceContact.getPriority() + 1; currentPriority < maxPriority; currentPriority++) {

            iceContactsDAO.updatePriority(currentPriority, (currentPriority - 1));
        }

        // TODO Priority
    }

    /**
     * Get maximum priority number
     *
     * @param context
     */
    public int getMaxPriority(Context context) {

        iceContactsDAO = new ICEContactsDAOImpl(context);

        return iceContactsDAO.findMaxPriority();
    }
}
