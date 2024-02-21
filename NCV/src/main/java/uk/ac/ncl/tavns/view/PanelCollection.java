package uk.ac.ncl.tavns.view;

public class PanelCollection {

    ButtonControlsPanel buttonControlsPanel;
    ChartsPanel chartsPanel;
    ConfigurationPanel configurationPanel;

    public PanelCollection(ButtonControlsPanel buttonControlsPanel, ChartsPanel chartsPanel, ConfigurationPanel configurationPanel) {
        this.buttonControlsPanel = buttonControlsPanel;
        this.chartsPanel = chartsPanel;
        this.configurationPanel = configurationPanel;
    }

    public ButtonControlsPanel getButtonControlsPanel() {
        return buttonControlsPanel;
    }

    public void setButtonControlsPanel(ButtonControlsPanel buttonControlsPanel) {
        this.buttonControlsPanel = buttonControlsPanel;
    }

    public ChartsPanel getChartsPanel() {
        return chartsPanel;
    }

    public void setChartsPanel(ChartsPanel chartsPanel) {
        this.chartsPanel = chartsPanel;
    }

    public ConfigurationPanel getConfigurationPanel() {
        return configurationPanel;
    }

    public void setConfigurationPanel(ConfigurationPanel configurationPanel) {
        this.configurationPanel = configurationPanel;
    }
}
