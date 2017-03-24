package com.aimsio;

import com.aimsio.component.SignalVisualizerChartView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.servlet.annotation.WebServlet;

@Theme("mytheme")
@Widgetset("com.aimsio.SignalVisualizerAppWidgetset")
@Title("Signal Visualizer")
public class SignalVisualizerApp extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        layout.addComponents(new SignalVisualizerChartView());
        layout.setSpacing(true);
        layout.setMargin(new MarginInfo(false, true));
        layout.setResponsive(true);
        setContent(layout);
    }

    @WebServlet(urlPatterns = "/*", name = "SignalVisualizerAppServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = SignalVisualizerApp.class, productionMode = false)
    public static class SignalVisualizerAppServlet extends VaadinServlet {
    }
}
