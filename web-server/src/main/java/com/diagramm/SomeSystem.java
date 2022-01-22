package com.diagramm;

import com.diagramm.client.KrokiClient;
import com.diagramm.entity.App;
import com.diagramm.entity.Group;
import com.diagramm.entity.Listing;
import com.diagramm.entity.PublicListing;
import com.google.gson.Gson;
import com.structurizr.Workspace;
import com.structurizr.io.plantuml.PlantUMLDiagram;
import com.structurizr.io.plantuml.StructurizrPlantUMLWriter;
import com.structurizr.model.Container;
import com.structurizr.model.CreateImpliedRelationshipsUnlessSameRelationshipExistsStrategy;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class SomeSystem {

    @Inject
    @RestClient
    KrokiClient krokiClient;

    public String prepareWorkspace(Integer num) {
        StringWriter stringWriter = new StringWriter();
        Collection<PlantUMLDiagram> plantUMLDiagrams = new StructurizrPlantUMLWriter().toPlantUMLDiagrams(generateWorkspace());
        PlantUMLDiagram plantUMLDiagram = new ArrayList<>(plantUMLDiagrams).get(num);
        String svg = krokiClient.svg(plantUMLDiagram.getDefinition());
        stringWriter.append(svg);

        return stringWriter.toString();
    }

    private Workspace generateWorkspace() {
        Workspace workspace = new Workspace("Workspace", "Workspace Structure");
        Model model = workspace.getModel();
        model.setImpliedRelationshipsStrategy(new CreateImpliedRelationshipsUnlessSameRelationshipExistsStrategy());
        fill(model);
        workspace.getViews().createDefaultViews();
        return workspace;
    }

    private void fill(Model model) {
        for (Listing listing : readPublicListing()) {
            String name = listing.getDnsName() + " System";
            prepareSystem(model.addSoftwareSystem(name), listing.getAppName());
        }
    }

    private void prepareSystem(SoftwareSystem system, String startApplication) {
        Map<String, Container> apps = new HashMap<>();
        Map<String, List<String>> refsMap = new HashMap<>();

        for (Group group : readGroups()) {
            for (App app : group.getApps()) {
                refsMap.put(app.getName(), app.getRefs());
            }
        }
        Set<String> processedRefs = new HashSet<>();
        Set<String> actualApps = getRefs(startApplication, refsMap, processedRefs);
        log.info("Actual apps for app: {} is {}", startApplication, actualApps.size());

        for (Group group : readGroups()) {
            for (App app : group.getApps()) {
                if (!actualApps.contains(app.getName())) continue;
                apps.put(app.getName(), system.addContainer(app.getName()));
            }
        }

        Set<String> processed = new HashSet<>();
        setDeps(startApplication, apps, refsMap, processed);
    }

    private void setDeps(String application, Map<String, Container> apps, Map<String, List<String>> refsMap, Set<String> processed) {
        processed.add(application);
        List<String> refs = refsMap.get(application);
        if (refs != null && !refs.isEmpty()) {
            Container container = apps.get(application);
            if (container != null) {
                for (String refApp : refs) {
                    Container refContainer = apps.get(refApp);
                    if (refContainer != null) {
                        container.uses(refContainer, "related");
                    }
                    if (!processed.contains(refApp)) {
                        setDeps(refApp, apps, refsMap, processed);
                    }
                }
            }
        }
    }

    private Set<String> getRefs(String application, Map<String, List<String>> refsMap, Set<String> processedRefs) {
        processedRefs.add(application);
        HashSet<String> apps = new HashSet<>();
        apps.add(application);
        List<String> refsForApp = refsMap.get(application);
        if (refsForApp == null || refsForApp.isEmpty() || "keycloak".equals(application)) {
            return apps;
        } else {
            for (String refApp : refsForApp) {
                if (!processedRefs.contains(refApp)) {
                    apps.addAll(getRefs(refApp, refsMap, processedRefs));
                }
            }
        }
        return apps;
    }

    private List<Listing> readPublicListing() {
        Gson gson = new Gson();
        return gson.fromJson(readFile(resolve("public-listing.json").toFile()), PublicListing.class).getPublicListing();
    }

    private List<Group> readGroups() {
        Gson gson = new Gson();
        return getFiles(resolve("groups")).stream()
                .map(this::readFile)
                .map(s -> gson.fromJson(s, Group.class))
                .collect(Collectors.toList());
    }

    private String readFile(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            log.warn("Cannot read file: {}", file, e);
            return null;
        }
    }

    private Path resolve(String path) {
        return Path.of(System.getenv("HOME")).resolve("work").resolve("gitops").resolve("definitions").resolve(path);
    }

    private List<File> getFiles(Path path) {
        File[] files = path.toFile().listFiles();
        if (files == null) {
            return List.of();
        }
        return List.of(files);
    }
}
