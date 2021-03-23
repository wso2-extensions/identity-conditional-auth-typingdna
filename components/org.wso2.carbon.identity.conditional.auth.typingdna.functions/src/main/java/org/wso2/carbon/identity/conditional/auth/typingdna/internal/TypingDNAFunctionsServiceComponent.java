/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.conditional.auth.typingdna.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.identity.application.authentication.framework.JsFunctionRegistry;
import org.wso2.carbon.identity.conditional.auth.typingdna.SaveUserInTypingDNAFunction;
import org.wso2.carbon.identity.conditional.auth.typingdna.SaveUserInTypingDNAFunctionImpl;
import org.wso2.carbon.identity.conditional.auth.typingdna.TypingDNAConfigImpl;
import org.wso2.carbon.identity.conditional.auth.typingdna.VerifyUserWithTypingDNAFunction;
import org.wso2.carbon.identity.conditional.auth.typingdna.VerifyUserWithTypingDNAFunctionImpl;
import org.wso2.carbon.identity.governance.IdentityGovernanceService;
import org.wso2.carbon.identity.governance.common.IdentityConnectorConfig;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * OSGi declarative services component which handled registration and un-registration of TypingDNA related
 * functions.
 */

@Component(
        name = "custom.auth.tdnafunctions.component",
        immediate = true
)

public class TypingDNAFunctionsServiceComponent {

    private static final Log LOG = LogFactory.getLog(TypingDNAFunctionsServiceComponent.class);
    private static final String VERIFY_FUNCTION = "verifyUserWithTypingDNA";
    private static final String SAVE_FUNCTION = "saveUserInTypingDNA";

    private static JsFunctionRegistry jsFunctionRegistry;

    @Activate
    protected void activate(ComponentContext ctxt) {

        try {
            JsFunctionRegistry jsFunctionRegistry = TypingDNAFunctionsServiceHolder.getInstance().getJsFunctionRegistry();
            VerifyUserWithTypingDNAFunction verifyUserWithTypingDNAFunctionImpl = new VerifyUserWithTypingDNAFunctionImpl();
            jsFunctionRegistry.register(JsFunctionRegistry.Subsystem.SEQUENCE_HANDLER, VERIFY_FUNCTION,
                    verifyUserWithTypingDNAFunctionImpl);

            SaveUserInTypingDNAFunction saveUserInTypingDNAFunctionImpl = new SaveUserInTypingDNAFunctionImpl();
            jsFunctionRegistry.register(JsFunctionRegistry.Subsystem.SEQUENCE_HANDLER, SAVE_FUNCTION,
                    saveUserInTypingDNAFunctionImpl);

            BundleContext bundleContext = ctxt.getBundleContext();
            TypingDNAConfigImpl analyticsFunctionConfig = new TypingDNAConfigImpl();
            bundleContext.registerService(IdentityConnectorConfig.class.getName(), analyticsFunctionConfig, null);

        } catch (Throwable e) {
            LOG.error("Error while activating CustomAuthTdnaFunctionComponent", e);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext ctxt) {

        if (jsFunctionRegistry != null) {
            jsFunctionRegistry.deRegister(JsFunctionRegistry.Subsystem.SEQUENCE_HANDLER, VERIFY_FUNCTION);
            jsFunctionRegistry.deRegister(JsFunctionRegistry.Subsystem.SEQUENCE_HANDLER, SAVE_FUNCTION);
        }
    }

    @Reference(
            name = "user.realmservice.default",
            service = RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService"
    )
    protected void setRealmService(RealmService realmService) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("RealmService is set in the custom conditional authentication user functions bundle");
        }
        TypingDNAFunctionsServiceHolder.getInstance().setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("RealmService is unset in the custom conditional authentication user functions bundle");
        }
        TypingDNAFunctionsServiceHolder.getInstance().setRealmService(null);
    }

    @Reference(
            name = "registry.service",
            service = RegistryService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRegistryService"
    )
    protected void setRegistryService(RegistryService registryService) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("RegistryService is set in the custom conditional authentication user functions bundle");
        }
        TypingDNAFunctionsServiceHolder.getInstance().setRegistryService(registryService);
    }

    protected void unsetRegistryService(RegistryService registryService) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("RegistryService is unset in the custom conditional authentication user functions bundle");
        }
        TypingDNAFunctionsServiceHolder.getInstance().setRegistryService(null);
    }

    @Reference(
            service = JsFunctionRegistry.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetJsFunctionRegistry"
    )
    public void setJsFunctionRegistry(JsFunctionRegistry jsFunctionRegistry) {

        TypingDNAFunctionsServiceHolder.getInstance().setJsFunctionRegistry(jsFunctionRegistry);
    }

    public void unsetJsFunctionRegistry(JsFunctionRegistry jsFunctionRegistry) {

        TypingDNAFunctionsServiceHolder.getInstance().setJsFunctionRegistry(null);
    }

    @Reference(
            name = "identity.governance.service",
            service = IdentityGovernanceService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetIdentityGovernanceService"
    )
    protected void setIdentityGovernanceService(IdentityGovernanceService identityGovernanceService) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Identity Governance service is set form functions");
        }
        TypingDNAFunctionsServiceHolder.getInstance().setIdentityGovernanceService(identityGovernanceService);

        // Do nothing. Wait for the service before registering the governance connector.
    }

    protected void unsetIdentityGovernanceService(IdentityGovernanceService identityGovernanceService) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Identity Governance service is unset from functions");
        }
        TypingDNAFunctionsServiceHolder.getInstance().setIdentityGovernanceService(null);
        // Do nothing.
    }

}
