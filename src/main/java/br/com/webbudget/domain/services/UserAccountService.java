package br.com.webbudget.domain.services;

import br.com.webbudget.domain.entities.security.Authorization;
import br.com.webbudget.domain.entities.security.Grant;
import br.com.webbudget.domain.entities.security.Group;
import br.com.webbudget.domain.entities.security.User;
import br.com.webbudget.domain.repositories.tools.AuthorizationRepository;
import br.com.webbudget.domain.repositories.tools.GrantRepository;
import br.com.webbudget.domain.repositories.tools.GroupRepository;
import br.com.webbudget.domain.repositories.tools.UserRepository;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 27/12/2017
 */
@ApplicationScoped
public class UserAccountService {

    @Inject
    private UserRepository userRepository;
    @Inject
    private GrantRepository grantRepository;
    @Inject
    private GroupRepository groupRepository;
    @Inject
    private AuthorizationRepository authorizationRepository;

    /**
     *
     * @param user
     * @return
     */
    @Transactional
    public User save(User user) {
        return this.userRepository.save(user);
    }

    /**
     *
     * @param user
     */
    @Transactional
    public void update(User user) {
        this.userRepository.saveAndFlushAndRefresh(user);
    }

    /**
     *
     * @param user
     */
    @Transactional
    public void delete(User user) {
        this.userRepository.attachAndRemove(user);
    }

    /**
     *
     * @param group
     * @return
     */
    @Transactional
    public Group save(Group group) {
        return this.groupRepository.save(group);
    }

    /**
     *
     * @param group
     * @param authorizations
     */
    @Transactional
    public void save(Group group, List<Authorization> authorizations) {

        this.groupRepository.save(group);

        authorizations.stream().forEach(authz -> {

            Authorization authorization = this.authorizationRepository
                    .findOptionalByFunctionalityAndPermission(
                            authz.getFunctionality(), authz.getPermission())
                    .get();

            this.grantRepository.save(new Grant(group, authorization));
        });
    }

    /**
     *
     * @param group
     */
    @Transactional
    public void update(Group group) {
        this.groupRepository.saveAndFlushAndRefresh(group);
    }

    /**
     *
     * @param group
     * @param authorizations
     */
    @Transactional
    public void update(Group group, List<Authorization> authorizations) {

        this.groupRepository.saveAndFlushAndRefresh(group);

        // lista o grants antigos para deletar
        final List<Grant> oldGrants = this.grantRepository.findByGroup(group);

        oldGrants.stream().forEach(grant -> {
            this.grantRepository.remove(grant);
        });

        // grava os novos
        authorizations.stream().forEach(authz -> {

            Authorization authorization = this.authorizationRepository
                    .findOptionalByFunctionalityAndPermission(
                            authz.getFunctionality(), authz.getPermission())
                    .get();

            this.grantRepository.save(new Grant(group, authorization));
        });
    }

    /**
     *
     * @param group
     */
    @Transactional
    public void delete(Group group) {
        this.groupRepository.attachAndRemove(group);
    }

    /**
     *
     * @param authorization
     */
    @Transactional
    public void save(Authorization authorization) {
        this.authorizationRepository.saveAndFlush(authorization);
    }

    /**
     *
     * @param grant
     * @return
     */
    @Transactional
    public Grant save(Grant grant) {
        return this.grantRepository.save(grant);
    }

    /**
     *
     * @param grant
     */
    @Transactional
    public void update(Grant grant) {
        this.grantRepository.saveAndFlushAndRefresh(grant);
    }

    /**
     *
     * @param grant
     */
    @Transactional
    public void remove(Grant grant) {
        this.grantRepository.attachAndRemove(grant);
    }

    /**
     *
     * @param authorizations
     * @param group
     */
    @Transactional
    public void grantAll(List<Authorization> authorizations, Group group) {
        authorizations.stream().forEach(authz -> {
            this.grantRepository.save(new Grant(group, authz));
        });
    }

    /**
     *
     * @param username
     * @return
     */
    public User findUserByUsername(String username) {
        return this.userRepository.findOptionalByUsername(username)
                .orElse(null);
    }
}
