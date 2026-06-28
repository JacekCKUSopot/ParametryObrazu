import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class ParametryObrazu extends JFrame {

    // Suwaki JSlider do regulowania jasności, kontrastu oraz nasycenia koloru
    private final JSlider suwakJasnosci = new JSlider(0, 200, 100);
    private final JSlider suwakKontrastu = new JSlider(0, 200, 100);
    private final JSlider suwakNasycenia = new JSlider(0, 200, 100);

    // Etykiety wyświetlające bieżące wartości liczbowe suwaków
    private final JLabel etykietaJasnosci = new JLabel("Jasność: 100");
    private final JLabel etykietaKontrastu = new JLabel("Kontrast: 100");
    private final JLabel etykietaNasycenia = new JLabel("Nasycenie: 100");


    // Panel prezentujący kolor podglądu wygenerowany ze stanu suwaków
    private final JPanel panelPodgladu = new JPanel();
    private final JButton przyciskZapisz = new JButton("Zapisz ustawienia");
    private final JButton przyciskWczytaj = new JButton("Wczytaj ustawienia");
    private final JButton przyciskReset = new JButton("Reset");

    // Nazwa pliku z zapisanymi parametrami suwaków
    private static final String NAZWA_PLIKU = "ustawienia_obrazu.txt";

    public ParametryObrazu() {
        setTitle("Ustawienia parametrów obrazu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 420);
        setLocationRelativeTo(null);

        konfigurujSuwak(suwakJasnosci);
        konfigurujSuwak(suwakKontrastu);
        konfigurujSuwak(suwakNasycenia);

        panelPodgladu.setPreferredSize(new Dimension(200, 150));
        panelPodgladu.setBackground(new Color(128, 128, 128));
        panelPodgladu.setBorder(BorderFactory.createTitledBorder("Podgląd koloru"));

        JPanel panelSuwakow = new JPanel(new GridLayout(6, 2, 5, 8));
        panelSuwakow.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        panelSuwakow.add(etykietaJasnosci);
        panelSuwakow.add(suwakJasnosci);
        panelSuwakow.add(etykietaKontrastu);
        panelSuwakow.add(suwakKontrastu);
        panelSuwakow.add(etykietaNasycenia);
        panelSuwakow.add(suwakNasycenia);

        JPanel panelPrzyciskow = new JPanel(new FlowLayout());
        panelPrzyciskow.add(przyciskZapisz);
        panelPrzyciskow.add(przyciskWczytaj);
        panelPrzyciskow.add(przyciskReset);

        setLayout(new BorderLayout());
        add(panelSuwakow, BorderLayout.NORTH);
        add(panelPodgladu, BorderLayout.CENTER);
        add(panelPrzyciskow, BorderLayout.SOUTH);

        // Rejestracja ChangeListenera reagującego na przesuwanie suwaka w czasie rzeczywistym
        suwakJasnosci.addChangeListener(e -> {
            etykietaJasnosci.setText("Jasność: " + suwakJasnosci.getValue());
            aktualizujPodglad();
        });
        suwakKontrastu.addChangeListener(e -> {
            etykietaKontrastu.setText("Kontrast: " + suwakKontrastu.getValue());
            aktualizujPodglad();
        });
        suwakNasycenia.addChangeListener(e -> {

            etykietaNasycenia.setText("Nasycenie: " + suwakNasycenia.getValue());
            aktualizujPodglad();
        });

        przyciskZapisz.addActionListener(e -> zapiszUstawienia());
        przyciskWczytaj.addActionListener(e -> wczytajUstawienia());
        przyciskReset.addActionListener(e -> resetujSuwaki());
    }

    // Pomocnicza metoda ustawiająca główne i pomocnicze kreski podziałki oraz ich etykiety liczbowe
    private void konfigurujSuwak(JSlider suwak) {
        suwak.setMajorTickSpacing(50);
        suwak.setMinorTickSpacing(10);
        suwak.setPaintTicks(true);
        suwak.setPaintLabels(true);
    }

    // Przelicza składowe koloru RGB na podstawie wartości suwaków i odświeża tło panelu
    private void aktualizujPodglad() {
        int jasnosc = suwakJasnosci.getValue();
        int nasycenie = suwakNasycenia.getValue();
        int wartoscKoloru = Math.min(255, jasnosc);
        int czerwony = Math.min(255, (int) (wartoscKoloru * nasycenie / 100.0));
        int zielony = Math.min(255, wartoscKoloru / 2);
        int niebieski = Math.min(255, (int) (wartoscKoloru * (200 - nasycenie) / 200.0));
        panelPodgladu.setBackground(new Color(czerwony, zielony, niebieski));
    }

    // Przywraca wszystkie trzy suwaki do ich domyślnej wartości środkowej (100)
    private void resetujSuwaki() {
        suwakJasnosci.setValue(100);
        suwakKontrastu.setValue(100);
        suwakNasycenia.setValue(100);
    }

    // Zapisuje bieżące wartości parametrów w formacie klucz=wartość do pliku
    private void zapiszUstawienia() {
        try (BufferedWriter zapisywacz = new BufferedWriter(new FileWriter(NAZWA_PLIKU))) {
            zapisywacz.write("jasnosc=" + suwakJasnosci.getValue());
            zapisywacz.newLine();
            zapisywacz.write("kontrast=" + suwakKontrastu.getValue());
            zapisywacz.newLine();
            zapisywacz.write("nasycenie=" + suwakNasycenia.getValue());
            JOptionPane.showMessageDialog(this, "Ustawienia zapisane do: " + NAZWA_PLIKU);
        } catch (IOException wyjatek) {
            JOptionPane.showMessageDialog(this, "Błąd zapisu: " + wyjatek.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Odczytuje właściwości z pliku i przywraca odpowiednie położenia suwaków
    private void wczytajUstawienia() {
        Map<String, Integer> ustawienia = new HashMap<>();
        try (BufferedReader czytnik = new BufferedReader(new FileReader(NAZWA_PLIKU))) {
            String linia;

            while ((linia = czytnik.readLine()) != null) {
                String[] czesci = linia.split("=");
                if (czesci.length == 2) {
                    try {
                        ustawienia.put(czesci[0].trim(), Integer.parseInt(czesci[1].trim()));
                    } catch (NumberFormatException wyjatek) {
                        JOptionPane.showMessageDialog(this,
                                "Błędna wartość w pliku: " + linia,
                                "Błąd danych", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
            if (ustawienia.containsKey("jasnosc"))
                suwakJasnosci.setValue(ustawienia.get("jasnosc"));
            if (ustawienia.containsKey("kontrast"))
                suwakKontrastu.setValue(ustawienia.get("kontrast"));
            if (ustawienia.containsKey("nasycenie"))
                suwakNasycenia.setValue(ustawienia.get("nasycenie"));

            JOptionPane.showMessageDialog(this, "Wczytano ustawienia.");
        } catch (FileNotFoundException wyjatek) {
            JOptionPane.showMessageDialog(this, "Plik ustawień nie istnieje.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException wyjatek) {
            JOptionPane.showMessageDialog(this, "Błąd odczytu: " + wyjatek.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Zastąpiono SwingUtilities.invokeLater bezpośrednim wywołaniem konstruktora i setVisible
        ParametryObrazu przyklad = new ParametryObrazu();
        przyklad.setVisible(true);
    }
}