package au.id.vanlaatum.botter.transport.slack.Modal;

import au.id.vanlaatum.botter.transport.slack.Modal.RTM.BaseEvent;
import au.id.vanlaatum.botter.transport.slack.Modal.RTM.BasePacket;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class BaseEventTest {
  @Test
  public void name () throws Exception {
    ObjectMapper mapper = new ObjectMapper ();
    BasePacket packet;
    JsonNode tree = mapper.readTree ( "{\n" +
        "  \"type\": \"message\",\n" +
        "  \"channel\": \"C213WG23G\",\n" +
        "  \"user\": \"U1SSV0WBE\",\n" +
        "  \"text\": \"l\",\n" +
        "  \"ts\": \"1471079970.000010\",\n" +
        "  \"team\": \"T1ST53GLV\"\n" +
        "}" );
    packet = mapper.convertValue ( tree, BaseEvent.getClassForPacket ( tree ) );

    tree = mapper.readTree ( "{\n" +
        "    \"user\": \"U1YT89TST\",\n" +
        "    \"inviter\": \"U1SSV0WBE\",\n" +
        "    \"user_profile\": {\n" +
        "        \"avatar_hash\": \"850158c6b2c1\",\n" +
        "        \"image_72\": \"https://avatars.slack-edge.com/2016-08-06/66929770897_850158c6b2c184242262_72.png\",\n" +
        "        \"first_name\": null,\n" +
        "        \"real_name\": \"\",\n" +
        "        \"name\": \"bot\"\n" +
        "    },\n" +
        "    \"type\": \"message\",\n" +
        "    \"subtype\": \"channel_join\",\n" +
        "    \"team\": \"T1ST53GLV\",\n" +
        "    \"text\": \"<@U1YT89TST|bot> has joined the channel\",\n" +
        "    \"channel\": \"C213WG23G\",\n" +
        "    \"ts\": \"1471076889.000009\"\n" +
        "}\n" );
    packet = mapper.convertValue ( tree, BaseEvent.getClassForPacket ( tree ) );

    tree = mapper.readTree ( "{\n" +
        "  \"type\": \"message\",\n" +
        "  \"message\": {\n" +
        "    \"type\": \"message\",\n" +
        "    \"user\": \"U1SSV0WBE\",\n" +
        "    \"text\": \"<https://valexdevteam.slack.com/archives/test/p1471161063000014>\",\n" +
        "    \"attachments\": [\n" +
        "      {\n" +
        "        \"from_url\": \"https://valexdevteam.slack.com/archives/test/p1471161063000014\",\n" +
        "        \"fallback\": \"[August 14th, 2016 12:51 AM] dvanlaatum: <https://valexdevteam.slack.com/team/dvanlaatum>\",\n" +
        "        \"ts\": \"1471161063.000014\",\n" +
        "        \"author_subname\": \"dvanlaatum\",\n" +
        "        \"channel_id\": \"C213WG23G\",\n" +
        "        \"channel_name\": \"test\",\n" +
        "        \"is_msg_unfurl\": true,\n" +
        "        \"text\": \"<https://valexdevteam.slack.com/team/dvanlaatum>\",\n" +
        "        \"author_name\": \"David van Laatum\",\n" +
        "        \"author_link\": \"https://valexdevteam.slack.com/team/dvanlaatum\",\n" +
        "        \"author_icon\": \"https://secure.gravatar.com/avatar/f902b9ce67a91f0c35a076f2c7b07614.jpg?s=48&d=https%3A%2F%2Fa.slack-edge.com%2F66f9%2Fimg%2Favatars%2Fava_0013-48.png\",\n" +
        "        \"mrkdwn_in\": [\n" +
        "          \"text\"\n" +
        "        ],\n" +
        "        \"id\": 1,\n" +
        "        \"footer\": \"Posted in #test\"\n" +
        "      }\n" +
        "    ],\n" +
        "    \"ts\": \"1471161107.000016\"\n" +
        "  },\n" +
        "  \"subtype\": \"message_changed\",\n" +
        "  \"hidden\": true,\n" +
        "  \"channel\": \"C213WG23G\",\n" +
        "  \"previous_message\": {\n" +
        "    \"type\": \"message\",\n" +
        "    \"user\": \"U1SSV0WBE\",\n" +
        "    \"text\": \"<https://valexdevteam.slack.com/archives/test/p1471161063000014>\",\n" +
        "    \"ts\": \"1471161107.000016\"\n" +
        "  },\n" +
        "  \"event_ts\": \"1471161108.193769\",\n" +
        "  \"ts\": \"1471161108.000017\"\n" +
        "}\n" );
/*    packet = mapper.convertValue ( tree, BaseEvent.getClassForPacket ( tree ) );

    tree = mapper.readTree (
        "{\n" +
            "  \"type\": \"user_change\",\n" +
            "  \"user\": {\n" +
            "    \"id\": \"U1YT89TST\",\n" +
            "    \"team_id\": \"T1ST53GLV\",\n" +
            "    \"name\": \"bot\",\n" +
            "    \"deleted\": false,\n" +
            "    \"status\": null,\n" +
            "    \"color\": \"db3150\",\n" +
            "    \"real_name\": \"Mr Bot\",\n" +
            "    \"tz\": null,\n" +
            "    \"tz_label\": \"Pacific Daylight Time\",\n" +
            "    \"tz_offset\": -25200,\n" +
            "    \"profile\": {\n" +
            "      \"bot_id\": \"B1YT89TSB\",\n" +
            "      \"api_app_id\": \"\",\n" +
            "      \"avatar_hash\": \"15cb6614628a\",\n" +
            "      \"image_24\": \"https://avatars.slack-edge.com/2016-08-14/69258496642_15cb6614628afe33b4d4_24.png\",\n" +
            "      \"image_32\": \"https://avatars.slack-edge.com/2016-08-14/69258496642_15cb6614628afe33b4d4_32.png\",\n" +
            "      \"image_48\": \"https://avatars.slack-edge.com/2016-08-14/69258496642_15cb6614628afe33b4d4_48.png\",\n" +
            "      \"image_72\": \"https://avatars.slack-edge.com/2016-08-14/69258496642_15cb6614628afe33b4d4_48.png\",\n" +
            "      \"image_192\": \"https://avatars.slack-edge.com/2016-08-14/69258496642_15cb6614628afe33b4d4_48.png\",\n" +
            "      \"image_512\": \"https://avatars.slack-edge.com/2016-08-14/69258496642_15cb6614628afe33b4d4_48.png\",\n" +
            "      \"image_1024\": \"https://avatars.slack-edge.com/2016-08-14/69258496642_15cb6614628afe33b4d4_48.png\",\n" +
            "      \"image_original\": \"https://avatars.slack-edge.com/2016-08-14/69258496642_15cb6614628afe33b4d4_original.png\",\n" +
            "      \"first_name\": \"Mr\",\n" +
            "      \"last_name\": \"Bot\",\n" +
            "      \"title\": \"Stuff\",\n" +
            "      \"real_name\": \"Mr Bot\",\n" +
            "      \"real_name_normalized\": \"Mr Bot\",\n" +
            "      \"fields\": null\n" +
            "    },\n" +
            "    \"is_admin\": false,\n" +
            "    \"is_owner\": false,\n" +
            "    \"is_primary_owner\": false,\n" +
            "    \"is_restricted\": false,\n" +
            "    \"is_ultra_restricted\": false,\n" +
            "    \"is_bot\": true\n" +
            "  },\n" +
            "  \"cache_ts\": 1471162298,\n" +
            "  \"event_ts\": \"1471162298.195542\"\n" +
            "}\n" );
    packet = mapper.convertValue ( tree, BaseEvent.getClassForPacket ( tree ) );*/

    tree = mapper.readTree (
        "{\n" +
            "  \"type\": \"channel_marked\",\n" +
            "  \"channel\": \"C213WG23G\",\n" +
            "  \"ts\": \"1472091296.000009\",\n" +
            "  \"unread_count\": 0,\n" +
            "  \"unread_count_display\": 0,\n" +
            "  \"num_mentions\": 0,\n" +
            "  \"num_mentions_display\": 0,\n" +
            "  \"mention_count\": 0,\n" +
            "  \"mention_count_display\": 0,\n" +
            "  \"event_ts\": \"1472091298.411025\"\n" +
            "}" );

    packet = mapper.convertValue ( tree, BaseEvent.getClassForPacket ( tree ) );

    tree = mapper.readTree (
        "{\n" +
            "  \"type\": \"im_marked\",\n" +
            "  \"channel\": \"D213CG14G\",\n" +
            "  \"ts\": \"1472091732.000014\",\n" +
            "  \"dm_count\": 0,\n" +
            "  \"unread_count_display\": 0,\n" +
            "  \"num_mentions_display\": 0,\n" +
            "  \"mention_count_display\": 0,\n" +
            "  \"event_ts\": \"1472091737.413702\"\n" +
            "}" );

    packet = mapper.convertValue ( tree, BaseEvent.getClassForPacket ( tree ) );

    tree = mapper.readTree ( "{\n" +
        "  \"text\": \"Pssst! I didn’t unfurl <https://github.com/FasterXML/jackson-core> because it was already shared in this channel quite recently (within the last hour) and I didn’t want to clutter things up.\",\n" +
        "  \"is_ephemeral\": true,\n" +
        "  \"username\": \"slackbot\",\n" +
        "  \"type\": \"message\",\n" +
        "  \"subtype\": \"bot_message\",\n" +
        "  \"channel\": \"D213CG14G\",\n" +
        "  \"ts\": \"1472290473.000000\",\n" +
        "  \"msg\": {\n" +
        "    \"do_not_unfurl_links\": true\n" +
        "  },\n" +
        "  \"event_ts\": \"1472290473.585625\"\n" +
        "}" );

    packet = mapper.convertValue ( tree, BaseEvent.getClassForPacket ( tree ) );

    tree = mapper.readTree ( "{\n" +
        "  \"type\": \"message\",\n" +
        "  \"message\": {\n" +
        "    \"type\": \"message\",\n" +
        "    \"user\": \"U1SSV0WBE\",\n" +
        "    \"text\": \"<https://github.com/FasterXML/jackson-core>\",\n" +
        "    \"attachments\": [\n" +
        "      {\n" +
        "        \"service_name\": \"GitHub\",\n" +
        "        \"title\": \"FasterXML/jackson-core\",\n" +
        "        \"title_link\": \"https://github.com/FasterXML/jackson-core\",\n" +
        "        \"text\": \"jackson-core - Core part of Jackson that defines Streaming API as well as basic shared abstractions\",\n" +
        "        \"fallback\": \"GitHub: FasterXML/jackson-core\",\n" +
        "        \"thumb_url\": \"https://avatars3.githubusercontent.com/u/382692?v=3&s=400\",\n" +
        "        \"from_url\": \"https://github.com/FasterXML/jackson-core\",\n" +
        "        \"thumb_width\": 400,\n" +
        "        \"thumb_height\": 400,\n" +
        "        \"service_icon\": \"https://github.com/apple-touch-icon.png\",\n" +
        "        \"id\": 1\n" +
        "      }\n" +
        "    ],\n" +
        "    \"ts\": \"1472290472.000035\"\n" +
        "  },\n" +
        "  \"subtype\": \"message_changed\",\n" +
        "  \"hidden\": true,\n" +
        "  \"channel\": \"D213CG14G\",\n" +
        "  \"previous_message\": {\n" +
        "    \"type\": \"message\",\n" +
        "    \"user\": \"U1SSV0WBE\",\n" +
        "    \"text\": \"<https://github.com/FasterXML/jackson-core>\",\n" +
        "    \"ts\": \"1472290472.000035\"\n" +
        "  },\n" +
        "  \"event_ts\": \"1472290473.585623\",\n" +
        "  \"ts\": \"1472290473.000036\"\n" +
        "}" );

    packet = mapper.convertValue ( tree, BaseEvent.getClassForPacket ( tree ) );
  }
}
