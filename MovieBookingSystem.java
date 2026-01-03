import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class MovieBookingSystem {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private Map<String, String> users = new HashMap<>();
    private ArrayList<Movie> movies = new ArrayList<>();
    private String selectedMovie;
    private String selectedTime;
    private ArrayList<String> selectedSeats = new ArrayList<>();
    private double totalPrice = 0;

    private JPanel confirmationPanel;
    private JPanel movieSelectionPanel;

    public static void main(String[] args) 
    {
        EventQueue.invokeLater(() -> {
            try {
                MovieBookingSystem window = new MovieBookingSystem();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MovieBookingSystem() 
    {
        setupUsers();
        setupMovies();
        initialize();
    }

    private void initialize() 
    {
        frame = new JFrame("Movie Ticket Booking System");
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createLoginPanel();
        createMovieSelectionPanel();
        createSeatSelectionPanel();
        createPaymentPanel();
        createConfirmationPanel();

        frame.getContentPane().add(mainPanel);

        cardLayout.show(mainPanel, "Login");
    }

    private void setupUsers() 
    {
        users.put("user1", "password1");
        users.put("user2", "password2");
        users.put("admin", "admin123");
    }

    private void setupMovies() {
        movies.add(new Movie("The Avengers", "Action", "10:00 AM", "Theatre 1", 200));
        movies.add(new Movie("Inception", "Sci-Fi", "1:00 PM", "Theatre 2", 200));
        movies.add(new Movie("The Dark Knight", "Action", "4:00 PM", "Theatre 3", 200));
    }

    private void createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Login for Booking");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);

        JTextField usernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());
            if (users.containsKey(user) && users.get(user).equals(pass)) {
                refreshMovieList();
                cardLayout.show(mainPanel, "MovieSelection");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainPanel.add(panel, "Login");
    }

    private void createMovieSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Select a Movie", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        movieSelectionPanel = new JPanel();
        movieSelectionPanel.setLayout(new BoxLayout(movieSelectionPanel, BoxLayout.Y_AXIS));
        panel.add(new JScrollPane(movieSelectionPanel), BorderLayout.CENTER);

        mainPanel.add(panel, "MovieSelection");
    }

    private void refreshMovieList() {
        movieSelectionPanel.removeAll();

        for (Movie m : movies) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JLabel name = new JLabel(m.name);
            name.setFont(new Font("Arial", Font.BOLD, 16));

            JTextArea info = new JTextArea("Genre: " + m.genre + "\nShowtime: " + m.time + "\nTheatre: " + m.theatre + "\nPrice: Rs." + m.price);
            info.setEditable(false);
            info.setOpaque(false);

            JButton select = new JButton("Select");
            select.addActionListener(e -> {
                selectedMovie = m.name;
                selectedTime = m.time;
                totalPrice = m.price;
                selectedSeats.clear();
                cardLayout.show(mainPanel, "SeatSelection");
            });

            card.add(name, BorderLayout.NORTH);
            card.add(info, BorderLayout.CENTER);
            card.add(select, BorderLayout.SOUTH);

            movieSelectionPanel.add(card);
        }

        movieSelectionPanel.revalidate();
        movieSelectionPanel.repaint();
    }

    private void createSeatSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Select Seats", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        JPanel seatPanel = new JPanel(new GridLayout(5, 8, 5, 5));
        for (int i = 0; i < 40; i++) {
            String seatNum = "S" + (i + 1);
            JButton seatBtn = new JButton(seatNum);
            seatBtn.setBackground(Color.GREEN);

            if (Math.random() < 0.2) {
                seatBtn.setBackground(Color.RED);
                seatBtn.setEnabled(false);
            } else {
                seatBtn.addActionListener(new ActionListener() {
                    boolean isSelected = false;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (isSelected) {
                            selectedSeats.remove(seatNum);
                            seatBtn.setBackground(Color.GREEN);
                        } else {
                            selectedSeats.add(seatNum);
                            seatBtn.setBackground(Color.BLUE);
                        }
                        isSelected = !isSelected;
                    }
                });
            }

            seatPanel.add(seatBtn);
        }

        panel.add(seatPanel, BorderLayout.CENTER);

        JButton next = new JButton("Proceed to Payment");
        next.addActionListener(e -> {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Select at least one seat", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                totalPrice = selectedSeats.size() * 200;
                cardLayout.show(mainPanel, "Payment");
            }
        });

        panel.add(next, BorderLayout.SOUTH);
        mainPanel.add(panel, "SeatSelection");
    }

    private void createPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Payment", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JTextField cardField = new JTextField();
        JTextField expField = new JTextField();
        JPasswordField cvvField = new JPasswordField();
        JTextField nameField = new JTextField();

        form.add(new JLabel("Card Number:")); form.add(cardField);
        form.add(new JLabel("Expiry Date:")); form.add(expField);
        form.add(new JLabel("CVV:")); form.add(cvvField);
        form.add(new JLabel("Cardholder Name:")); form.add(nameField);

        panel.add(form, BorderLayout.CENTER);

        JButton pay = new JButton("Confirm Payment");
        pay.addActionListener(e -> {
            if (cardField.getText().isEmpty() || expField.getText().isEmpty() || new String(cvvField.getPassword()).isEmpty() || nameField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                updateConfirmationPanel();
                cardLayout.show(mainPanel, "Confirmation");
            }
        });

        panel.add(pay, BorderLayout.SOUTH);
        mainPanel.add(panel, "Payment");
    }

    private void createConfirmationPanel() {
        confirmationPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Booking Confirmed", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        confirmationPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(confirmationPanel, "Confirmation");
    }

    private void updateConfirmationPanel() {
        confirmationPanel.removeAll();
        JLabel title = new JLabel("Booking Confirmed", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        confirmationPanel.add(title, BorderLayout.NORTH);

        JPanel info = new JPanel(new GridLayout(0, 1, 5, 5));
        info.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        info.add(new JLabel("Movie: " + selectedMovie));
        info.add(new JLabel("Time: " + selectedTime));
        info.add(new JLabel("Seats: " + String.join(", ", selectedSeats)));
        info.add(new JLabel(String.format("Total Paid: Rs.%.2f", totalPrice)));
        info.add(new JLabel("Ref#: " + UUID.randomUUID().toString().substring(0, 8)));

        JButton done = new JButton("Done");
        done.addActionListener(e -> {
            selectedSeats.clear();
            selectedMovie = null;
            selectedTime = null;
            totalPrice = 0;
            cardLayout.show(mainPanel, "Login");
        });

        confirmationPanel.add(info, BorderLayout.CENTER);
        confirmationPanel.add(done, BorderLayout.SOUTH);
        confirmationPanel.revalidate();
        confirmationPanel.repaint();
    }

    class Movie 
    {
        String name, genre, time, theatre;
        double price;

        public Movie(String name, String genre, String time, String theatre, double price) {
            this.name = name;
            this.genre = genre;
            this.time = time;
            this.theatre = theatre;
            this.price = price;
        }
    }
}