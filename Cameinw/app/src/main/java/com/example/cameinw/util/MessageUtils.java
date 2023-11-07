package com.example.cameinw.util;

import com.example.cameinw.dto.MessageDTO;
import com.example.cameinw.model.Message;
import com.example.cameinw.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageUtils {

    private static final SimpleDateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
    private static final SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public static void processMessageDTOs(List<MessageDTO> messageDTOs, List<Message> messages, List<Integer> userIdsToFetchImagesFor) {
        for (MessageDTO messageDTO : messageDTOs) {
            Message message = convertToMessage(messageDTO);
            messages.add(message);

            userIdsToFetchImagesFor.add(messageDTO.getSenderId());
            userIdsToFetchImagesFor.add(messageDTO.getReceiverId());
        }
    }

    public static Message convertToMessage(MessageDTO messageDTO) {
        Message message = new Message();
        User sender = new User(); // Create a new User instance without arguments
        User receiver = new User(); // Create a new User instance without arguments

        sender.setId(messageDTO.getSenderId());
        sender.setTheUserName(messageDTO.getSenderUsername());
        receiver.setId(messageDTO.getReceiverId());
        receiver.setTheUserName(messageDTO.getReceiverUsername());

        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMessage(messageDTO.getMessage());
        message.setTimestamp(messageDTO.getMessageTimestamp());

        return message;
    }

    public static Date parseTimestamp(String timestamp) {
        Date timestampDate = null;
        try {
            timestampDate = iso8601DateFormat.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestampDate;
    }

    public static String formatTimestamp(Date timestampDate) {
        String formattedTimestamp = null;
        if (timestampDate != null) {
            formattedTimestamp = outputDateFormat.format(timestampDate);
        }
        return formattedTimestamp;
    }
}
