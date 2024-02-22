package uk.ac.ncl.tavns.view;

public class PanelCollection {

    private ButtonControlsPanel buttonControlsPanel;
    private ChartsPanel chartsPanel;
    private ConfigurationPanel configurationPanel;
    private StimulationConfigurationPanel stimulationConfiguration;

    public PanelCollection(ButtonControlsPanel buttonControlsPanel, ChartsPanel chartsPanel, ConfigurationPanel configurationPanel, StimulationConfigurationPanel stimulationConfiguration) {
        this.buttonControlsPanel = buttonControlsPanel;
        this.chartsPanel = chartsPanel;
        this.configurationPanel = configurationPanel;
        this.stimulationConfiguration = stimulationConfiguration;
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

    public StimulationConfigurationPanel getStimulationConfiguration() {
        return stimulationConfiguration;
    }

    public void setStimulationConfiguration(StimulationConfigurationPanel stimulationConfiguration) {
        this.stimulationConfiguration = stimulationConfiguration;
    }

}
