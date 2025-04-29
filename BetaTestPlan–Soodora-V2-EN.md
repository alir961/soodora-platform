# Beta Test Plan – Soodora

## 1. Selection of Key Features

The beta version of Soodora must demonstrate its core value while ensuring a stable and coherent experience for early testers. After analysis, we have identified three key features to include:

| **Feature**                                                               | **Description**                                                                                                                                                                                                                                                            | **Priority (High/Medium/Low)** |
|---------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------|
| Recommendation algorithm for discovering artists and content             | Designed to highlight the most engaging content for a user and help users discover emerging artists                                                                                                                                                                        | Medium                        |
| Artist subscription and personalized content                             | On the Explore page, two sections: one for discovering new artists and trends, and a personalized page dedicated to subscriptions. Users can subscribe to their favorite artists and easily access their featured posts in this exclusive space.                           | Medium                        |
| Link page creation tool                                                  | Allows artists to create a customizable link page that gathers their streaming platforms, social media or other links, styled according to Soodora’s brand identity, responsive on all devices, with integrated click tracking and analytics.                              | Medium                        |
| Content creation and export for artists                                  | Artists will be able to publish engaging posts in various formats: announcements, music releases, events, merch drops, etc. Each post will have an optimized visual rendering to capture attention and can be exported and customized in a format suited for social media. | High                          |

### Change Since Tech3
Soodora shifted direction between Tech3 and Tech4 following concrete market feedback and an internal reassessment. This repositioning was accompanied by a complete rebranding, with a new visual identity. Soodora is a platform designed to help artists gain exposure and grow, while offering music enthusiasts a simple way to discover new talent. With an interactive exploration page, a powerful post export tool, a link page creation tool, and an integrated marketplace, Soodora creates a direct connection between artists and their audience.

---

## 2. Beta Test Scenarios
### 2.1 User Roles

| **Role**             | **Description**                                                                                                                                                                                                                                                                                                                                  |
|----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Standard User        | For music enthusiasts wanting to discover new talent. Standard users have full access to Soodora to discover new artists via the "explore" page, subscribe to artists, interact with posted content, and enjoy a personalized recommendation algorithm.                                                                                       |
| Standard Artist      | For artists looking to gain exposure and engage their audience. Standard artists can create, publish, and export various content formats (announcements, music releases, events, etc.), create and customize their link page, access the marketplace, and develop their community via the platform.                                        |
| Premium Artist       | Premium artists enjoy increased visibility, watermark-free export tools, and higher revenue opportunities through lower merch fees, for example. This status allows optimal promotion and distribution of their musical universe.                                                                                                                  |

### 2.2 Test Scenarios

We have developed several test scenarios to validate these features and ensure a smooth user experience.

#### Scenario 1: Personalized Recommendations
- **User Role:** Standard User
- **Feature Tested:** Recommendation algorithm
- **Objective:** Verify that the user receives relevant recommendations based on their activity and subscriptions.
- **Preconditions:** The user has an active account and is logged in.
- **Test Steps:**
  1. Create an account and browse the Explore section
  2. Interact with several posts and products in the marketplace
  3. Check if recommendations evolve based on interactions
  4. Ensure recommendations are relevant and diverse
- **Expected Outcome:** The user receives recommendations tailored to their preferences and interactions. The content diversity is maintained. Recommendations dynamically evolve with new interactions.

#### Scenario 2: Subscription and Content Personalization
- **User Role:** Standard User
- **Feature Tested:** Artist subscription system
- **Objective:** Verify that subscriptions are correctly registered and that content from followed artists is featured prominently.
- **Preconditions:** The user has an active account and is logged in. Artists are available in the Explore section.
- **Test Steps:**
  1. Subscribe to multiple artists from the Explore section
  2. Access the Explore and Marketplace sections to see if followed artists’ content is highlighted
  3. Verify subscription management (unsubscribe, feed updates)
- **Expected Outcome:** Followed artists’ content appears first in Explore and the Marketplace. The user can easily unsubscribe and see real-time feed updates. The algorithm adjusts content prominence based on subscriptions.

#### Scenario 3: Link Page Creation and Data Analytics
- **User Role:** Artist
- **Feature Tested:** Link page creation system and analytics
- **Objective:** Ensure that the link page layout matches the Soodora brand guidelines, is accessible via external browsers, and that all links are clickable. Verify that click counts are recorded and processed.
- **Preconditions:** The artist is logged into their account. The link page creation tool is available.
- **Test Steps:**
  1. Open the link page creation tool
  2. Add links and specify the platform to display the corresponding logo
  3. Verify the link page is accessible externally (artist, authenticated user, unauthenticated user)
  4. Verify the artist can access data related to this page (click counts, top link)
- **Expected Outcome:** The link page is successfully generated. The layout follows Soodora branding. Each link displays the correct platform logo. All links are clickable and redirect correctly. The page URL is public and accessible by anyone. No display or redirection bugs. The artist has real-time access to page analytics.

#### Scenario 4: Post Creation
- **User Role:** Artist
- **Feature Tested:** Post creation
- **Objective:** Verify that artists can create posts.
- **Preconditions:** The artist is logged into their account. The post creation tool is available.
- **Test Steps:**
  1. Open the post creation tool
  2. Add content (text, image, music link, product)
  3. Publish and check display in the application
- **Expected Outcome:** The artist can add various content types without errors. The post displays correctly with well-formatted elements. Users can view and interact with the post.

#### Scenario 5: Post Export and Customization
- **User Role:** Premium Artist
- **Feature Tested:** Post export and customization
- **Objective:** Ensure that artists can efficiently export and customize posts for social media via the export tool.
- **Preconditions:** The Premium Artist is logged in. The export tool is available. The artist has at least one published post.
- **Test Steps:**
  1. Open the post creation tool
  2. Add content (text, image, music link, product)
  3. Publish and check in the application
  4. Open the export tool
  5. Customize the post
  6. Export and publish the design on a social network, then verify layout
- **Expected Outcome:** Post customization and export occur without technical errors. Layout is preserved in the exported design and on the social network.

---

## 3. Coverage of Key User Journeys

The test scenarios reflect real user usage. The key journeys validated in this phase are:

- **Discovering new artists and products via the recommendation algorithm**
- **Following artists and personalizing displayed content**
- **Smooth interaction between artists and their audience through posts, a link page, and customized exports to other platforms**

---

## 4. Success Criteria

To validate the success of the beta, we use the following criteria:

- **Stability**: The application must operate without critical errors or crashes that could disrupt the user experience.
- **Performance**: Page loads and recommendations must be optimized for smooth, responsive navigation.
- **Recommendation Relevance**: The suggestion algorithm must deliver content aligned with user preferences. Effectiveness will be measured by engagement rates (clicks, shares, listening time) on recommendations.
- **Subscription System Effectiveness**: Subscribed users must see their followed artists’ posts prominently and accessibly in their personalized space, ensuring real added value.
- **Link Page Creation and Personalization**:
  1. **Workflow**: Intuitive process for adding, rearranging, and styling links in a guided interface.
  2. **Personalization**: Theme and color palette selection compliant with Soodora branding, link ordering, and cover image addition.
  3. **Responsive Display**: Error-free, optimal rendering on mobile and desktop, with a dashboard for click tracking.
- **Publication Export Quality**:
  1. **Render Fidelity**: Exact preservation of layout, visuals, and typography.
  2. **Export Workflow**: Real-time preview before publishing and one-click export to multiple channels.

---

## 5. Known Bugs and Limitations

| **Limitation**          | **Description**                                                                                                                                                            | **Impact** | **Fix Scheduled? (Yes/No)**               |
|-------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------|-------------------------------------------|
| Dispersed community     | Difficulty attracting artists and fans simultaneously to a new platform.                                                                                                 | High      | Yes (strong marketing campaign)           |
| Active user base        | Need for an active user base to enable relevant recommendations and ensure visibility for emerging artists.                                                              | High      | Yes (focus on value proposition)         |

---

## 6. Conclusion

This test plan is not only a technical roadmap: it's the Soodora’s commitment to revolutionize the way artists and their audiences interact. By rigorously evaluating the beta version, we aim to confirm the stability, performance, and relevance of each feature.

This plan serves as a vital lever for gathering constructive feedback that will guide subsequent development phases. The tests cover scenarios ranging from personalized recommendations to artist interactions via content creation and export. They offer a comprehensive view of real Soodora usage. The insights gained will not only correct potential bugs but also strengthen Soodora’s value proposition.

Ultimately, this approach aims to transform Soodora into a must-have platform in the music world, ensuring a smooth transition to a stable and complete version. This test plan marks the launch of Soodora’s vision: establishing a direct, innovative, and lasting link between artists and their audience.

_Soodora – where music finds its audience._

