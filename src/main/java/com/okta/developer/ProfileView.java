package com.okta.developer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * Managed bean which exposes the pac4j profile manager.
 *
 * JSF views such as facelets can reference this to view the contents of profiles.
 *
 * @author Phillip Ross
 */
@Named
@RequestScoped
public class ProfileView {

    /** The static logger instance. */
    private static final Logger logger = LoggerFactory.getLogger(ProfileView.class);

    /** The pac4j web context. */
    @Inject
    private WebContext webContext;

    /** The pac4j profile manager. */
    @Inject
    private ProfileManager profileManager;

    /** Simple no-args constructor. */
    public ProfileView() {
    }

    /**
     * Gets the first profile (if it exists) contained in the profile manager.
     *
     * @return a list of pac4j profiles
     */
    public Object getProfile() {
        return profileManager.get(true).orElse(null); // It's fine to return a null reference if there is no value present.
    }

    /**
     * Gets the profiles contained in the profile manager.
     *
     * @return a list of pac4j profiles
     */
    public List getProfiles() {
        return profileManager.getAll(true);
    }

    /** Simply prints some debugging information post-construction. */
    @PostConstruct
    public void init() {
        logger.debug("webContext is null? {}", (webContext == null));
        logger.debug("profileManager is null? {}", (profileManager == null));
    }
}
