package br.edu.ifsuldeminas.sd.chat.client;

import br.edu.ifsuldeminas.sd.chat.MessageContainer;

public class SysOutContainer implements MessageContainer {
    public void newMessage(String message) {
        if (message == null || message.isEmpty())
            return;
        String[] messageParts = message.split(MessageContainer.FROM);

        System.out.printf("%s> %s%n", messageParts[1].trim(), messageParts[0].trim());
    }
}
