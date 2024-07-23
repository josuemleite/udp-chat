package br.edu.ifsuldeminas.sd.chat.swing;

import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatSwing {

    private JFrame frame;
    private JTextField localPortField;
    private JTextField remotePortField;
    private JTextField nameField;
    private JButton connectButton;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private String userName;

    private Sender sender;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatSwing().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("SI.zap");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(4, 2));
        localPortField = new JTextField();
        remotePortField = new JTextField();
        nameField = new JTextField();
        connectButton = new JButton("Conectar");

        JLabel localPort = new JLabel("Porta local:");
        JLabel remotePort = new JLabel("Porta remota:");
        JLabel userName = new JLabel("Nome:");

        int padding = 10;
        localPort.setBorder(new EmptyBorder(padding, padding, padding, padding));
        remotePort.setBorder(new EmptyBorder(padding, padding, padding, padding));
        userName.setBorder(new EmptyBorder(padding, padding, padding, padding));

        topPanel.add(localPort);
        topPanel.add(localPortField);
        topPanel.add(remotePort);
        topPanel.add(remotePortField);
        topPanel.add(userName);
        topPanel.add(nameField);
        topPanel.add(new JLabel());
        topPanel.add(connectButton);

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        messageField = new JTextField();
        sendButton = new JButton("Enviar");
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        bottomPanel.add(inputPanel, BorderLayout.SOUTH);
        bottomPanel.setEnabled(false);
        enableBottomPanel(false);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(bottomPanel, BorderLayout.CENTER);

        connectButton.addActionListener(new ConnectButtonListener());
        sendButton.addActionListener(new SendButtonListener());

        frame.setVisible(true);
    }

    private void enableBottomPanel(boolean enable) {
        bottomPanel.setEnabled(enable);
        messageField.setEnabled(enable);
        sendButton.setEnabled(enable);
    }

    private class ConnectButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int localPort = Integer.parseInt(localPortField.getText());
            int remotePort = Integer.parseInt(remotePortField.getText());
            userName = nameField.getText();
            try {
                sender = ChatFactory.build("localhost", remotePort, localPort, new SwingMessageContainer());
                enableBottomPanel(true);
                topPanel.setEnabled(false);
                localPortField.setEnabled(false);
                remotePortField.setEnabled(false);
                nameField.setEnabled(false);
                connectButton.setEnabled(false);
            } catch (ChatException chatException) {
                JOptionPane.showMessageDialog(frame, "Erro ao conectar: " + chatException.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                message = String.format("%s%s%s", message, MessageContainer.FROM, userName);
                try {
                    sender.send(message);
                    messageField.setText("");
                } catch (ChatException chatException) {
                    JOptionPane.showMessageDialog(frame, "Erro ao enviar mensagem: " + chatException.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class SwingMessageContainer implements MessageContainer {
        @Override
        public void newMessage(String message) {
            if (message == null || message.isEmpty()) {
                return;
            }
            String[] messageParts = message.split(MessageContainer.FROM);
            SwingUtilities.invokeLater(() -> chatArea.append(String.format("%s> %s%n", messageParts[1].trim(), messageParts[0].trim())));
        }
    }
}
