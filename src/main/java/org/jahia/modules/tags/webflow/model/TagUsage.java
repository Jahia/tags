/**
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2016 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ==================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     This program is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU General Public License
 *     as published by the Free Software Foundation; either version 2
 *     of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 *     2/ JSEL - Commercial and Supported Versions of the program
 *     ===================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE JSEL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     Alternatively, commercial and supported versions of the program - also known as
 *     Enterprise Distributions - must be used in accordance with the terms and conditions
 *     contained in a separate written agreement between you and Jahia Solutions Group SA.
 *
 *     If you are unsure which license is appropriate for your use,
 *     please contact the sales department at sales@jahia.com.
 */
package org.jahia.modules.tags.webflow.model;

import java.io.Serializable;

/**
 * Created by kevan on 16/09/15.
 */
public class TagUsage implements Serializable{
    private static final long serialVersionUID = -5381427731859991771L;

    String displayablePath;
    String displayableName;
    String taggedNodePath;
    String taggedNodeIdentifier;

    public String getDisplayablePath() {
        return displayablePath;
    }

    public void setDisplayablePath(String displayablePath) {
        this.displayablePath = displayablePath;
    }

    public String getDisplayableName() {
        return displayableName;
    }

    public void setDisplayableName(String displayableName) {
        this.displayableName = displayableName;
    }

    public String getTaggedNodePath() {
        return taggedNodePath;
    }

    public void setTaggedNodePath(String taggedNodePath) {
        this.taggedNodePath = taggedNodePath;
    }

    public String getTaggedNodeIdentifier() {
        return taggedNodeIdentifier;
    }

    public void setTaggedNodeIdentifier(String taggedNodeIdentifier) {
        this.taggedNodeIdentifier = taggedNodeIdentifier;
    }
}
