// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.prototype;

import java.util.Date;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public class AbstractUserEntity {
    protected int userId;
    protected String userFirstName;
    protected String userLastName;
    protected Date lastVisitDate;
    protected AbstractRoleEntity role;
}
