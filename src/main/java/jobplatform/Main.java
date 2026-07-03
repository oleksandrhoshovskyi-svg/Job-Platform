package jobplatform;

import jobplatform.gui.MainFrame;
import jobplatform.persistence.DataStore;
import jobplatform.persistence.SampleData;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        boolean loaded = DataStore.load();

        if (!loaded) {
            System.out.println("First run — loading sample data into H2 database...");
            SampleData.load();
        } else {
            System.out.println("Data loaded from H2 database.");
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}