package net.sin;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;

/**
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@Push
@PWA(name = "Project Base for Vaadin with Spring", shortName = "Project Base", enableInstallPrompt = false)
public class AppShell implements AppShellConfigurator {
}
