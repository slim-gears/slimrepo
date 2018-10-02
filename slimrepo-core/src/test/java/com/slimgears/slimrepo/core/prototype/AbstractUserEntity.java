// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.prototype;

import com.slimgears.slimrepo.core.prototype.generated.AccountStatus;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Denis on 07-Apr-15
 *
 */
public class AbstractUserEntity {
    protected String userId;
    protected String userFirstName;
    protected String userLastName;
    protected Date lastVisitDate;
    protected AbstractRoleEntity role;
    protected AccountStatus accountStatus;
    protected ArrayList<String> comments;
    protected int age;
}
