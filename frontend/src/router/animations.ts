import { Animation, createAnimation } from '@ionic/vue';

/**
 * Custom fade transition for all route changes
 */
export const fadeTransition = (
  _: HTMLElement,
  opts: { leavingEl?: HTMLElement; enteringEl?: HTMLElement }
): Animation => {
  const DURATION = 350;

  const rootAnimation = createAnimation();

  // Fade out the leaving view
  if (opts.leavingEl) {
    const leavingAnimation = createAnimation()
      .addElement(opts.leavingEl)
      .duration(DURATION)
      .easing('ease-in-out')
      .fromTo('opacity', 1, 0);

    rootAnimation.addAnimation(leavingAnimation);
  }

  // Fade in the entering view
  if (opts.enteringEl) {
    const enteringAnimation = createAnimation()
      .addElement(opts.enteringEl)
      .duration(DURATION)
      .easing('ease-in-out')
      .fromTo('opacity', 0, 1);

    rootAnimation.addAnimation(enteringAnimation);
  }

  return rootAnimation;
};
