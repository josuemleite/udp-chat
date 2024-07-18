package br.edu.ifsuldeminas.sd.chat.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.Scanner;
import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;

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
    private Sender sender;
    private String userName;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatSwing().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Chat Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(4, 2));
        localPortField = new JTextField();
        remotePortField = new JTextField();
        nameField = new JTextField();
        connectButton = new JButton("Conectar");

        topPanel.add(new JLabel("Porta local:"));
        topPanel.add(localPortField);
        topPanel.add(new JLabel("Porta remota:"));
        topPanel.add(remotePortField);
        topPanel.add(new JLabel("Nome:"));
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
            if (!message.equals("")) {
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
            if (message == null || message.equals(""))
                return;
            String[] messageParts = message.split(MessageContainer.FROM);
            SwingUtilities.invokeLater(() -> chatArea.append(String.format("%s> %s%n", messageParts[1], messageParts[0])));
        }
    }
}
