/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtree.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.screens.guided.dtree.client.resources.GuidedDecisionTreeResources;
import org.drools.workbench.screens.guided.dtree.client.resources.i18n.GuidedDecisionTreeConstants;
import org.drools.workbench.screens.guided.dtree.client.type.GuidedDTreeResourceType;
import org.drools.workbench.screens.guided.dtree.service.GuidedDecisionTreeEditorService;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.services.shared.resources.EditorIds;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new Guided Decision Trees
 */
@ApplicationScoped
public class NewGuidedDecisionTreeHandler extends DefaultNewResourceHandler {

    private Caller<GuidedDecisionTreeEditorService> service;
    private GuidedDTreeResourceType resourceType;
    private BusyIndicatorView busyIndicatorView;
    private AuthorizationManager authorizationManager;
    private SessionInfo sessionInfo;

    public NewGuidedDecisionTreeHandler() {
        //CDI proxy
    }

    @Inject
    public NewGuidedDecisionTreeHandler(final Caller<GuidedDecisionTreeEditorService> service,
                                        final GuidedDTreeResourceType resourceType,
                                        final BusyIndicatorView busyIndicatorView,
                                        final AuthorizationManager authorizationManager,
                                        final SessionInfo sessionInfo) {
        this.service = service;
        this.resourceType = resourceType;
        this.busyIndicatorView = busyIndicatorView;
        this.authorizationManager = authorizationManager;
        this.sessionInfo = sessionInfo;
    }

    @Override
    public String getDescription() {
        return GuidedDecisionTreeConstants.INSTANCE.newGuidedDecisionTreeDescription();
    }

    @Override
    public IsWidget getIcon() {
        return new Image(GuidedDecisionTreeResources.INSTANCE.images().typeGuidedDecisionTree());
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public boolean canCreate() {
        return authorizationManager.authorize(new ResourceRef(EditorIds.GUIDED_DECISION_TREE,
                                                              ActivityResourceType.EDITOR),
                                              ResourceAction.READ,
                                              sessionInfo.getIdentity());
    }

    @Override
    public void create(final Package pkg,
                       final String baseFileName,
                       final NewResourcePresenter presenter) {
        final GuidedDecisionTree model = new GuidedDecisionTree();
        model.setTreeName(baseFileName);
        busyIndicatorView.showBusyIndicator(CommonConstants.INSTANCE.Saving());
        service.call(getSuccessCallback(presenter),
                     new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView)).create(pkg.getPackageMainResourcesPath(),
                                                                                         buildFileName(baseFileName,
                                                                                                       resourceType),
                                                                                         model,
                                                                                         "");
    }
}
