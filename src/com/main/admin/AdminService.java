package com.main.admin;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminService
{
    public static String getstatus(int status)
    {
        if(status == 0)
            return "<p class = \"btn btn-info\" style=\"width: 70px;\">Pending</p>";
        else if(status == 1)
            return "<p class = \"btn btn-success\" style=\"width: 70px;\">Accepted</p>";
        else return
                    "<p class = \"btn btn-danger\" style=\"width: 70px;\">Reject</p>";
    }
    public static String BuildHistory(String history, String username, int status, int newStatus)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        history += "<div>" + df.format(new Date()) + " " + username + ":" + AdminService.getstatus(status)
                + "<i class=\"fa fa-arrow-right\"></i>" + AdminService. getstatus(newStatus) + "</div>";
        return history;
    }
}
