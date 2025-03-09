# Beta Test Plan – Soodora

## 1. Selection of Core Features

The beta version of Soodora must demonstrate its core value while ensuring a stable and consistent experience for early testers. After analysis, we have identified three key features to include:

| **Feature**                                                           | **Description**                                                                                                                                                                                                                                                                | **Priority (High/Medium/Low)** |
|------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| Recommendation algorithm for discovering artists and content           | Designed to highlight the most engaging content for a user and help users discover emerging artists.                                                                                                                                                                           | Medium                             |
| Artist subscriptions and content personalization                       | The Explore page is divided into two sections: an exploration section to discover new artists and trends, and a personalized page dedicated to subscriptions. Users can subscribe to their favorite artists and easily access their highlighted posts in this exclusive space. | High                               |
| Content creation and export for artists                                | Artists can publish attractive posts in various formats: announcements, music releases, events, merch drops, etc. Each post will have an optimized visual rendering to capture attention and can be exported, personalized in a format suitable for social media.              | High                               |

### Changes Since Tech3
Soodora shifted direction between Tech3 and Tech4 based on concrete market feedback and internal team reflection. This repositioning was accompanied by a complete rebranding, with a new visual identity. Soodora is a platform designed to help artists gain visibility and grow, while offering music enthusiasts a simple way to discover new talents. With an interactive exploration page, a powerful post export tool, and an integrated marketplace, Soodora creates a direct link between artists and their audience.

---

## 2. Beta Test Scenarios
### 2.1 User Roles

| **Roles**            | **Description**                                                                                                                                                                                                                                                               |
|----------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Standard User        | This role is for music enthusiasts looking to discover new talents. Standard users have full access to Soodora to explore new artists via the "Explore" page, subscribe to artists, interact with posted content, and benefit from a personalized recommendation algorithm.   |
| Standard Artist      | This role is for artists who want to gain visibility and engage their audience. Standard artists can create, publish and export various content formats (announcements, music releases, events, etc.), access the marketplace, and grow their community through the platform. |
| Premium Artist       | Premium artists benefit from increased visibility, powerful export tools without watermark, and higher profitability opportunities, such as lower commissions on their merchandise. This status allows them to showcase and share their musical universe optimally.           |

### 2.2 Test Scenarios
We have developed several test scenarios to validate these features and ensure a smooth user experience.

#### Scenario 1 : Personalized Recommendations
- **User Role:** Standard User
- **Feature Tested:** Recommendation algorithm
- **Objective:** Verify that the user receives relevant recommendations based on their activity and subscriptions.
- **Preconditions:** The user has an active account and is logged in.
- **Test Steps:**
  1. Create an account and browse the Explore section
  2. Interact with several posts and marketplace products
  3. Check if recommendations evolve based on interactions
  4. Ensure recommendations are relevant and diverse
- **Expected Result:** The user receives recommendations tailored to their preferences and interactions. The diversity of recommended content is respected. Recommendations evolve dynamically based on new interactions.

#### Scenario 2 : Subscriptions and Content Personalization
- **User Role:** Standard User
- **Feature Tested:** Artist subscription system
- **Objective:** Verify that subscriptions are properly accounted for and that content from followed artists is prioritized.
- **Preconditions:** The user has an active account and is logged in. Artists are available in the Explore section.
- **Test Steps:**
  1. Subscribe to several artists from the Explore section
  2. Access the Explore and Marketplace sections to see if content from followed artists is highlighted
  3. Verify subscription management (unsubscribing, feed updates)
- **Expected Result:** Content from followed artists is prioritized in the Explore and Marketplace sections. The user can easily unsubscribe and see updates in real-time. The algorithm adjusts content prioritization based on the user’s subscriptions.

#### Scenario 3 : Post Creation
- **User Role:** Artist
- **Feature Tested:** Post creation
- **Objective:** Verify that artists can create posts.
- **Preconditions:** The artist is logged into their account. The post creation tool is accessible.
- **Test Steps:**
  1. Access the post creation tool
  2. Add content (text, image, music link, product)
  3. Publish and verify the display in the app
- **Expected Result:** The artist can add different types of content without errors. The post displays correctly in the app, with well-formatted elements. Users can view and interact with the post.

#### Scenario 4 : Post Export and Personalization
- **User Role:** Premium Artist
- **Feature Tested:** Post export and personalization
- **Objective:** Verify that artists can effectively export posts and personalized them into the export tool to their social media.
- **Preconditions:** The Premium Artist is logged into their account. The exportation tool is accessible. The artist has already published at least one post.
- **Test Steps:**
  1. Access the post creation tool
  2. Add content (text, image, music link, product)
  3. Publish and verify the display in the app
  4. Access to the export tool
  5. Personalize the post
  6. Export and publish the design on a social media platform and verify the layout
- **Expected Result:** The post can be personalized and exported without technical errors. The layout is preserved on the exported design and on the social media.

---

## 3. Coverage of Key User Journeys

The test scenarios are designed to reflect real user interactions. The key journeys validated in this phase are:

- **Discovering new artists and products via the recommendation algorithm**
- **Following artists and personalizing displayed content**
- **Artists interacting fluently with their audience through posts and personalized exports to other platforms**

---

## 4. Success Criteria

To validate the success of the beta, we use the following criteria:

- **Stability:** The app must function without critical errors or crashes that could disrupt the user experience.
- **Performance:** Page and recommendation loading times must be optimized to ensure smooth and responsive navigation.
- **Relevance of Recommendations:** The recommendation algorithm must suggest content aligned with user preferences. The system’s effectiveness will be measured by engagement rates on these recommendations, particularly through interactions generated around them.
- **Efficiency of the Subscription System:** Users subscribed to artists should see their posts clearly and accessibly highlighted in their personalized space. This criterion ensures that subscriptions offer real added value by facilitating access to users’ favorite content.
- **Quality of Post Exports:** When a post is exported, it must retain its original appearance and be adapted to the specifics of each target distribution channel . Optimized exports ensure a polished presentation and increased visibility of artists’ content.

---

## 5. Known Bugs and Limitations

| **Bug** | **Description** | **Impact** | **Planned Fix? (Yes/No)** |
|---------|-----------------|------------|---------------------------------|
| x       | x               | x          | No                             |

---

## 6. Conclusion

This test plan is not just a technical roadmap: it embodies Soodora’s commitment to revolutionizing how artists and their audiences interact. By rigorously evaluating the beta version, we aim to confirm the stability, performance, and relevance of each feature.

This plan is a crucial tool for gathering constructive feedback that will guide the next phases of development. The tests cover scenarios ranging from personalized recommendations to artist interactions through content creation and export, offering a comprehensive view of Soodora’s real-world use. The insights gained will not only address potential bugs but also strengthen Soodora’s value proposition.

In conclusion, this approach aims to establish Soodora as an essential platform in the music world, ensuring a smooth transition to a stable and complete version. This test plan is the launchpad toward realizing Soodora’s vision: creating a direct, innovative, and lasting connection between artists and their audience.
**_Soodora, where music finds its audience._**