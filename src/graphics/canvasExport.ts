import { StaticCanvas, FabricImage, Textbox, Rect, Circle, Group, Gradient, Shadow } from 'fabric';
import { Post } from '@/entity/Post';
import { Product } from '@/entity/Product';

type ExportTheme = 'black' | 'white' | 'transparent';

interface CustomColors {
  accent?: string;
  accentSecondary?: string;
}

interface ColorTheme {
  background: string;
  backgroundGradient: [string, string];
  cardGradient: [string, string];
  cardStroke: string;
  primary: string;
  secondary: string;
  accent: string;
  accentSecondary: string;
  accentGlow: string;
  chipBackground: string;
  chipText: string;
  surfaceShadow: string;
}

function hexToRgba(hex: string, alpha: number): string {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  if (!result) return `rgba(37, 99, 235, ${alpha})`;
  const r = parseInt(result[1], 16);
  const g = parseInt(result[2], 16);
  const b = parseInt(result[3], 16);
  return `rgba(${r}, ${g}, ${b}, ${alpha})`;
}

function retrieveColorTheme(theme: ExportTheme, customColors?: CustomColors): ColorTheme {
  const baseTheme = getBaseTheme(theme);

  if (customColors?.accent) {
    const accent = customColors.accent;
    const accentSecondary = customColors.accentSecondary || shiftHue(accent, 40);

    let chipTextColor: string;
    if (theme === 'black') {
      chipTextColor = accent;
    } else {
      chipTextColor = darkenColor(accent, 20);
    }

    return {
      ...baseTheme,
      accent: accent,
      accentSecondary: accentSecondary,
      accentGlow: hexToRgba(accent, 0.3),
      chipBackground: hexToRgba(accent, theme === 'black' ? 0.15 : 0.12),
      chipText: chipTextColor,
      cardStroke: hexToRgba(accent, theme === 'black' ? 0.15 : 0.2),
    };
  }

  return baseTheme;
}


function shiftHue(hex: string, degrees: number): string {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  if (!result) return hex;

  let r = parseInt(result[1], 16) / 255;
  let g = parseInt(result[2], 16) / 255;
  let b = parseInt(result[3], 16) / 255;

  const max = Math.max(r, g, b);
  const min = Math.min(r, g, b);
  let h = 0;
  let s = 0;
  const l = (max + min) / 2;

  if (max !== min) {
    const d = max - min;
    s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
    switch (max) {
      case r:
        h = ((g - b) / d + (g < b ? 6 : 0)) / 6;
        break;
      case g:
        h = ((b - r) / d + 2) / 6;
        break;
      case b:
        h = ((r - g) / d + 4) / 6;
        break;
    }
  }

  h = (h + degrees / 360) % 1;
  if (h < 0) h += 1;

  const hue2rgb = (p: number, q: number, t: number) => {
    if (t < 0) t += 1;
    if (t > 1) t -= 1;
    if (t < 1 / 6) return p + (q - p) * 6 * t;
    if (t < 1 / 2) return q;
    if (t < 2 / 3) return p + (q - p) * (2 / 3 - t) * 6;
    return p;
  };

  const q = l < 0.5 ? l * (1 + s) : l + s - l * s;
  const p = 2 * l - q;
  r = hue2rgb(p, q, h + 1 / 3);
  g = hue2rgb(p, q, h);
  b = hue2rgb(p, q, h - 1 / 3);

  const toHex = (x: number) => {
    const hex = Math.round(x * 255).toString(16);
    return hex.length === 1 ? '0' + hex : hex;
  };

  return `#${toHex(r)}${toHex(g)}${toHex(b)}`;
}

function darkenColor(hex: string, percent: number): string {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  if (!result) return hex;

  const r = Math.max(0, parseInt(result[1], 16) - (255 * percent) / 100);
  const g = Math.max(0, parseInt(result[2], 16) - (255 * percent) / 100);
  const b = Math.max(0, parseInt(result[3], 16) - (255 * percent) / 100);

  const toHex = (x: number) => {
    const hex = Math.round(x).toString(16);
    return hex.length === 1 ? '0' + hex : hex;
  };

  return `#${toHex(r)}${toHex(g)}${toHex(b)}`;
}

function getBaseTheme(theme: ExportTheme): ColorTheme {
  switch (theme) {
    case 'black':
      return {
        background: '#030712',
        backgroundGradient: ['#050814', '#101a2f'],
        cardGradient: ['rgba(15, 23, 42, 0.92)', 'rgba(8, 12, 24, 0.78)'],
        cardStroke: 'rgba(255, 255, 255, 0.08)',
        primary: '#f8fbff',
        secondary: '#97a4c7',
        accent: '#ffffff',
        accentSecondary: '#e2e8f0',
        accentGlow: 'rgba(255, 255, 255, 0.3)',
        chipBackground: 'rgba(255, 255, 255, 0.15)',
        chipText: '#ffffff',
        surfaceShadow: 'rgba(2, 6, 23, 0.6)',
      } as ColorTheme;
    case 'white':
      return {
        background: '#f1f5f9',
        backgroundGradient: ['#e2e8f0', '#f1f5f9'],
        cardGradient: ['rgba(255, 255, 255, 0.98)', 'rgba(248, 250, 255, 0.95)'],
        cardStroke: 'rgba(15, 23, 42, 0.12)',
        primary: '#0f172a',
        secondary: '#334155',
        accent: '#0f172a',
        accentSecondary: '#334155',
        accentGlow: 'rgba(15, 23, 42, 0.35)',
        chipBackground: 'rgba(15, 23, 42, 0.1)',
        chipText: '#0f172a',
        surfaceShadow: 'rgba(15, 23, 42, 0.15)',
      } as ColorTheme;
    case 'transparent':
      return {
        background: '#ffffff00',
        backgroundGradient: ['rgba(255, 255, 255, 0)', 'rgba(255, 255, 255, 0)'],
        cardGradient: ['rgba(255, 255, 255, 0.85)', 'rgba(248, 250, 255, 0.75)'],
        cardStroke: 'rgba(255, 255, 255, 0.3)',
        primary: '#0f172a',
        secondary: '#475569',
        accent: '#0f172a',
        accentSecondary: '#334155',
        accentGlow: 'rgba(15, 23, 42, 0.25)',
        chipBackground: 'rgba(15, 23, 42, 0.06)',
        chipText: '#0f172a',
        surfaceShadow: 'rgba(15, 23, 42, 0.12)',
      } as ColorTheme;
  }
}

function createGlassmorphicCard(
  x: number,
  y: number,
  width: number,
  height: number,
  colorTheme: ColorTheme,
  cornerRadius: number = 24,
  theme: ExportTheme = 'black'
): Rect {
  const gradient = new Gradient({
    type: 'linear',
    gradientUnits: 'pixels',
    coords: { x1: 0, y1: 0, x2: 0, y2: height },
    colorStops: [
      { offset: 0, color: colorTheme.cardGradient[0] },
      { offset: 1, color: colorTheme.cardGradient[1] },
    ],
  });

  const cardOptions: Partial<Rect> & { rx: number; ry: number } = {
    left: x,
    top: y,
    width: width,
    height: height,
    fill: gradient,
    rx: cornerRadius,
    ry: cornerRadius,
    stroke: theme === 'transparent' ? 'rgba(255, 255, 255, 0)' : colorTheme.cardStroke,
    strokeWidth: 1,
    selectable: false,
  };

  if (theme !== 'transparent') {
    cardOptions.shadow = new Shadow({
      color: colorTheme.surfaceShadow,
      blur: 80,
      offsetY: 40,
    });
  }

  return new Rect(cardOptions);
}

function updateCardAppearance(card: Rect, height: number, colorTheme: ColorTheme): void {
  const gradient = new Gradient({
    type: 'linear',
    gradientUnits: 'pixels',
    coords: { x1: 0, y1: 0, x2: 0, y2: height },
    colorStops: [
      { offset: 0, color: colorTheme.cardGradient[0] },
      { offset: 1, color: colorTheme.cardGradient[1] },
    ],
  });

  card.set({ height, fill: gradient });
  card.setCoords();
}

function createCircularAvatar(
  imageUrl: string,
  x: number,
  y: number,
  size: number,
  ringColor?: string
): Promise<Group> {
  return new Promise((resolve) => {
    FabricImage.fromURL(
      imageUrl,
      { crossOrigin: 'anonymous' },
      {
        left: 0,
        top: 0,
        selectable: false,
      }
    ).then((img) => {
      const sourceElement = img.getElement?.() ?? (img as unknown as { _element?: HTMLImageElement })._element;

      const tryCreateMaskedImage = (): FabricImage | null => {
        if (!!document?.createElement && sourceElement) {
          const workingCanvas = document.createElement('canvas');
          workingCanvas.width = size;
          workingCanvas.height = size;
          const ctx = workingCanvas.getContext('2d');
          if (!ctx) {
            return null;
          }

          const naturalWidth = sourceElement.width || img.width || size;
          const naturalHeight = sourceElement.height || img.height || size;
          const scale = Math.max(size / naturalWidth, size / naturalHeight);
          const scaledWidth = naturalWidth * scale;
          const scaledHeight = naturalHeight * scale;
          const offsetX = (scaledWidth - size) / 2;
          const offsetY = (scaledHeight - size) / 2;

          ctx.save();
          ctx.beginPath();
          ctx.arc(size / 2, size / 2, size / 2, 0, Math.PI * 2);
          ctx.closePath();
          ctx.clip();
          ctx.drawImage(sourceElement, -offsetX, -offsetY, scaledWidth, scaledHeight);
          ctx.restore();

          return new FabricImage(workingCanvas, {
            left: size / 2,
            top: size / 2,
            originX: 'center',
            originY: 'center',
            selectable: false,
          });
        }
        return null;
      };

      let avatarImage = tryCreateMaskedImage();

      if (!avatarImage) {
        const coverDiameter = size + 8;
        const baseWidth = img.width ?? size;
        const baseHeight = img.height ?? size;
        const scale = coverDiameter / Math.min(baseWidth, baseHeight);
        img.scale(scale);

        const clipCircle = new Circle({
          radius: size / 2,
          originX: 'center',
          originY: 'center',
          selectable: false,
        });

        img.clipPath = clipCircle;
        img.set({
          left: size / 2,
          top: size / 2,
          originX: 'center',
          originY: 'center',
          selectable: false,
        });

        avatarImage = img;
      }

      const halo = new Circle({
        radius: size / 2,
        left: size / 2,
        top: size / 2,
        originX: 'center',
        originY: 'center',
        fill: 'rgba(255, 255, 255, 0.03)',
        stroke: ringColor ?? 'rgba(255, 255, 255, 0.5)',
        strokeWidth: 4,
        selectable: false,
      });

      const group = new Group([halo, avatarImage], {
        left: x,
        top: y,
        width: size,
        height: size,
        selectable: false,
      });

      resolve(group);
    });
  });
}

type FabricTextboxOptions = NonNullable<ConstructorParameters<typeof Textbox>[1]>;

interface AutoSizeTextboxOptions extends Partial<Omit<FabricTextboxOptions, 'width'>> {
  maxWidth: number;
  maxHeight?: number;
  maxFontSize: number;
  minFontSize?: number;
  step?: number;
  truncateOverflow?: boolean;
  enforceSingleLine?: boolean;
}

function createAutoSizedTextbox(text: string, options: AutoSizeTextboxOptions): Textbox {
  const {
    maxWidth,
    maxHeight,
    maxFontSize,
    minFontSize = 20,
    step = 2,
    truncateOverflow = true,
    enforceSingleLine = false,
    ...textboxOptions
  } = options;

  const baseFontSize = textboxOptions.fontSize ?? maxFontSize;
  let workingText = text;

  const buildTextbox = (content: string, fontSize: number, allowSplitByGrapheme = false): Textbox => {
    const textbox = new Textbox(content, {
      ...textboxOptions,
      width: maxWidth,
      fontSize,

      splitByGrapheme: allowSplitByGrapheme,
    });
    textbox.initDimensions();
    return textbox;
  };

  const exceedsBounds = (textbox: Textbox): boolean => {
    const heightExceeded = maxHeight ? textbox.getScaledHeight() > maxHeight + 0.5 : false;

    if (!enforceSingleLine) {
      return heightExceeded;
    }

    const lineCount = textbox.textLines?.length ?? 1;
    const widestLine = (textbox.textLines ?? []).reduce((widest, _line, index) => {
      const measured = textbox.getLineWidth(index) * (textbox.scaleX ?? 1);
      return Math.max(widest, measured);
    }, 0);
    const widthExceeded = lineCount > 1 || widestLine > maxWidth - 0.5;
    return heightExceeded || widthExceeded;
  };

  const hasWordTooWide = (content: string, fontSize: number): boolean => {
    const words = content.split(/\s+/);
    for (const word of words) {
      const wordBox = buildTextbox(word, fontSize, false);
      const wordWidth = wordBox.getLineWidth(0) * (wordBox.scaleX ?? 1);
      if (wordWidth > maxWidth + 0.5) {
        return true;
      }
    }
    return false;
  };

  const shortenText = (content: string): string => {
    const cleaned = content.trim();
    if (cleaned.length <= 4) {
      return cleaned;
    }
    const ellipsis = '…';
    const normalized = cleaned.endsWith(ellipsis) ? cleaned.slice(0, -1) : cleaned;
    const words = normalized.split(/\s+/);
    if (words.length <= 1) {
      return `${normalized.slice(0, Math.max(1, normalized.length - 2)).trimEnd()}${ellipsis}`;
    }

    words.pop();
    return `${words.join(' ')}${ellipsis}`;
  };

  const truncateLongWord = (word: string, fontSize: number): string => {
    const ellipsis = '…';
    let truncated = word;

    while (truncated.length > 1) {
      const testText = truncated + ellipsis;
      const testBox = buildTextbox(testText, fontSize, false);
      const testWidth = testBox.getLineWidth(0) * (testBox.scaleX ?? 1);

      if (testWidth <= maxWidth) {
        return testText;
      }
      truncated = truncated.slice(0, -1);
    }

    return truncated + ellipsis;
  };

  const maxIterations = 50;
  for (let iteration = 0; iteration < maxIterations; iteration += 1) {
    let fontSize = Math.min(maxFontSize, baseFontSize);

    while (fontSize >= minFontSize) {
      if (!hasWordTooWide(workingText, fontSize)) {
        const candidate = buildTextbox(workingText, fontSize, false);
        if (!exceedsBounds(candidate)) {
          return candidate;
        }
      }
      fontSize -= step;
    }

    const words = workingText.split(/\s+/);
    if (words.length === 1 && hasWordTooWide(workingText, minFontSize)) {
      const truncatedWord = truncateLongWord(workingText, minFontSize);
      return buildTextbox(truncatedWord, minFontSize, false);
    }

    if (!truncateOverflow || !maxHeight) {
      break;
    }

    const shortened = shortenText(workingText);
    if (shortened === workingText) {
      break;
    }
    workingText = shortened;
  }

  const fallbackTextbox = buildTextbox(workingText, minFontSize, false);
  return fallbackTextbox;
}

function createExcerptCopy(text: string | undefined, maxCharacters: number): string {
  if (!text) {
    return '';
  }

  const trimmed = text.trim();
  if (trimmed.length <= maxCharacters) {
    return trimmed;
  }

  return `${trimmed.slice(0, maxCharacters).trim()}…`;
}

async function createPostCanvas(
  post: Post,
  theme: ExportTheme,
  customColors?: CustomColors
): Promise<StaticCanvas> {
  const colorTheme = retrieveColorTheme(theme, customColors);
  const canvas = new StaticCanvas(undefined, { width: 1080, height: 1920, backgroundColor: 'rgba(0,0,0,0)' });

  const canvasHeight = canvas.getHeight();
  const canvasWidth = canvas.getWidth();

  const cardLeft = 80;
  const cardWidth = canvasWidth - 160;
  const minCardTop = 180;
  const minCardBottom = 80;
  const maxCardHeight = canvasHeight - minCardTop - minCardBottom;

  const tempCardTop = 0;
  const horizontalPadding = 96;
  const contentX = cardLeft + horizontalPadding;
  const contentWidth = cardWidth - horizontalPadding * 2;

  const cardElements: {
    element: { set: (props: { top: number }) => void; setCoords: () => void; top?: number };
    relativeTop: number;
  }[] = [];

  let currentY = tempCardTop + 110;

  const avatarSize = 120;
  const authorAvatar = await createCircularAvatar(
    post.owner.avatarUrl ?? '/assets/images/svg/avatar.svg',
    contentX,
    currentY,
    avatarSize,
    colorTheme.accent
  );
  cardElements.push({ element: authorAvatar, relativeTop: currentY });

  const authorUsername = new Textbox(`@${post.owner.username}`, {
    top: currentY + 16,
    left: contentX + avatarSize + 32,
    width: contentWidth - avatarSize - 32,
    fontSize: 56,
    fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
    fontWeight: '700',
    fill: colorTheme.primary,
    selectable: false,
    textAlign: 'left',
  });
  cardElements.push({ element: authorUsername, relativeTop: currentY + 16 });

  const authorSubtitle = new Textbox('Soodora community · Spotlight', {
    top: currentY + 86,
    left: contentX + avatarSize + 32,
    width: contentWidth - avatarSize - 32,
    fontSize: 34,
    fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
    fontWeight: '500',
    fill: colorTheme.secondary,
    selectable: false,
    textAlign: 'left',
  });
  cardElements.push({ element: authorSubtitle, relativeTop: currentY + 86 });

  currentY += avatarSize + 70;

  const isAudioPostForChip = !!(post.audioUrl && post.audioTitle);
  const chipText = isAudioPostForChip ? 'SOODORA MUSIC' : 'SOODORA POST';
  const chipWidth = Math.min(contentWidth, 360);
  const chipBackground = new Rect({
    left: contentX,
    top: currentY,
    width: chipWidth,
    height: 64,
    rx: 32,
    ry: 32,
    fill: colorTheme.chipBackground,
    stroke: colorTheme.cardStroke,
    strokeWidth: 1,
    selectable: false,
  });
  cardElements.push({ element: chipBackground, relativeTop: currentY });

  const chipLabel = new Textbox(chipText, {
    top: currentY + 18,
    left: contentX,
    fontSize: 28,
    width: chipWidth,
    fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
    fontWeight: '600',
    fill: colorTheme.chipText,
    selectable: false,
    textAlign: 'center',
    charSpacing: 200,
  });
  cardElements.push({ element: chipLabel, relativeTop: currentY + 18 });

  currentY += 64 + 60;

  const heroWidth = contentWidth;
  const heroHeight = heroWidth * 0.62;
  const isAudioPost = !!(post.audioUrl && post.audioTitle);
  const titleMaxHeight = (post.imageUrl || isAudioPost) ? heroHeight * 0.45 : heroHeight * 0.85;

  // For audio posts, skip the post title section entirely - we show the track title instead
  if (!isAudioPost) {
    const postTitle = createAutoSizedTextbox(createExcerptCopy(post.title.toUpperCase(), 100), {
      top: currentY,
      left: contentX,
      maxWidth: contentWidth,
      maxHeight: titleMaxHeight,
      maxFontSize: 96,
      minFontSize: 58,
      fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
      fontWeight: '900',
      fill: colorTheme.primary,
      selectable: false,
      lineHeight: 1.05,
      textAlign: 'left',
      truncateOverflow: true,
    });
    postTitle.initDimensions();
    cardElements.push({ element: postTitle, relativeTop: currentY });
    currentY += postTitle.getScaledHeight() + 48;
  }

  const postTitleBottom = currentY;

  // Audio post with cover image
  if (isAudioPost && post.imageUrl) {
    const audioHeroHeight = heroWidth * 0.82;
    
    // Audio track title - with music icon
    const audioTrackTitle = createAutoSizedTextbox(`♫  ${createExcerptCopy(post.audioTitle.toUpperCase(), 50)}`, {
      top: currentY,
      left: contentX,
      maxWidth: contentWidth,
      maxHeight: 100,
      maxFontSize: 48,
      minFontSize: 30,
      fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
      fontWeight: '800',
      fill: colorTheme.accent,
      selectable: false,
      lineHeight: 1.1,
      textAlign: 'left',
      truncateOverflow: true,
    });
    audioTrackTitle.initDimensions();
    cardElements.push({ element: audioTrackTitle, relativeTop: currentY });
    currentY += audioTrackTitle.getScaledHeight() + 58;

    // More square-ish hero for album art (uses audioHeroHeight calculated above)
    
    const postImage = await FabricImage.fromURL(post.imageUrl, { crossOrigin: 'anonymous' }, { selectable: false });

    const imgWidth = postImage.width ?? 1;
    const imgHeight = postImage.height ?? 1;

    const scaleX = heroWidth / imgWidth;
    const scaleY = audioHeroHeight / imgHeight;
    const scale = Math.max(scaleX, scaleY);

    postImage.scaleX = scale;
    postImage.scaleY = scale;

    const clipRect = new Rect({
      width: heroWidth / scale,
      height: audioHeroHeight / scale,
      rx: 48 / scale,
      ry: 48 / scale,
      originX: 'center',
      originY: 'center',
    });

    postImage.set({
      left: contentX + heroWidth / 2,
      top: currentY + audioHeroHeight / 2,
      originX: 'center',
      originY: 'center',
      clipPath: clipRect,
      shadow: new Shadow({
        color: colorTheme.surfaceShadow,
        blur: 50,
        offsetY: 25,
      }),
    });
    postImage.setCoords();
    cardElements.push({ element: postImage, relativeTop: currentY + audioHeroHeight / 2 });

    // Dark overlay for audio
    const audioOverlay = new Rect({
      left: contentX,
      top: currentY,
      width: heroWidth,
      height: audioHeroHeight,
      rx: 48,
      ry: 48,
      fill: 'rgba(0, 0, 0, 0.4)',
      selectable: false,
    });
    cardElements.push({ element: audioOverlay, relativeTop: currentY });

    // Play button - bubble style like footer buttons
    const playButtonSize = 120;
    const playButtonGradient = new Gradient({
      type: 'linear',
      gradientUnits: 'pixels',
      coords: { x1: 0, y1: 0, x2: playButtonSize, y2: playButtonSize },
      colorStops: [
        { offset: 0, color: `${colorTheme.accent}33` },
        { offset: 0.5, color: `${colorTheme.accent}22` },
        { offset: 1, color: `${colorTheme.accent}2a` },
      ],
    });
    const playButton = new Circle({
      left: contentX + heroWidth / 2,
      top: currentY + audioHeroHeight / 2,
      radius: playButtonSize / 2,
      originX: 'center',
      originY: 'center',
      fill: playButtonGradient,
      stroke: `${colorTheme.accent}55`,
      strokeWidth: 2,
      selectable: false,
    });
    cardElements.push({ element: playButton, relativeTop: currentY + audioHeroHeight / 2 });

    // Play triangle icon
    const playTriangle = new Textbox('▶', {
      left: contentX + heroWidth / 2 + 5,
      top: currentY + audioHeroHeight / 2,
      width: 50,
      fontSize: 44,
      fontFamily: 'Arial, sans-serif',
      fill: colorTheme.accent,
      originX: 'center',
      originY: 'center',
      selectable: false,
      textAlign: 'center',
    });
    cardElements.push({ element: playTriangle, relativeTop: currentY + audioHeroHeight / 2 });

    // Dynamic waveform bars - more musical looking
    const barCount = 40;
    const barWidth = (heroWidth - 80) / barCount - 2.5;
    const maxBarHeight = 60;
    // Waveform baseline is exactly at the bottom of the image (inside rounded corners)
    const imageBottom = currentY + audioHeroHeight;
    const waveformBaseline = imageBottom;
    
    // Create a more dynamic, musical pattern with peaks and valleys
    const peakPositions = [0.15, 0.35, 0.5, 0.65, 0.85]; // Where the peaks are
    
    for (let i = 0; i < barCount; i++) {
      const progress = i / barCount;
      
      // Calculate distance to nearest peak for more musical feel
      let peakInfluence = 0;
      for (const peak of peakPositions) {
        const dist = Math.abs(progress - peak);
        peakInfluence = Math.max(peakInfluence, Math.max(0, 1 - dist * 4));
      }
      
      // Add some variation
      const variation = Math.sin(i * 1.2) * 0.15 + Math.cos(i * 0.7) * 0.1;
      const barHeight = Math.max(10, (0.25 + peakInfluence * 0.75 + variation) * maxBarHeight);
      
      const barGradient = new Gradient({
        type: 'linear',
        gradientUnits: 'pixels',
        coords: { x1: 0, y1: barHeight, x2: 0, y2: 0 },
        colorStops: [
          { offset: 0, color: `${colorTheme.accent}55` },
          { offset: 0.5, color: colorTheme.accent },
          { offset: 1, color: colorTheme.accentSecondary },
        ],
      });
      const bar = new Rect({
        left: contentX + 40 + i * (barWidth + 2.5),
        top: waveformBaseline - barHeight,
        width: barWidth,
        height: barHeight,
        rx: barWidth / 2,
        ry: barWidth / 2,
        fill: barGradient,
        selectable: false,
      });
      cardElements.push({ element: bar, relativeTop: waveformBaseline - barHeight });
    }

    currentY = waveformBaseline + 50;
  }
  // Audio post without cover image
  else if (isAudioPost && !post.imageUrl) {
    const audioHeroHeight = heroWidth * 0.58;
    
    // Audio track title - with music icon
    const audioTrackTitle = createAutoSizedTextbox(`♫  ${createExcerptCopy(post.audioTitle.toUpperCase(), 50)}`, {
      top: currentY,
      left: contentX,
      maxWidth: contentWidth,
      maxHeight: 100,
      maxFontSize: 48,
      minFontSize: 30,
      fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
      fontWeight: '800',
      fill: colorTheme.accent,
      selectable: false,
      lineHeight: 1.1,
      textAlign: 'left',
      truncateOverflow: true,
    });
    audioTrackTitle.initDimensions();
    cardElements.push({ element: audioTrackTitle, relativeTop: currentY });
    currentY += audioTrackTitle.getScaledHeight() + 40;

    // More square-ish hero for audio (uses audioHeroHeight calculated above)

    // Dark gradient panel for audio visualization
    const audioGradient = new Gradient({
      type: 'linear',
      gradientUnits: 'pixels',
      coords: { x1: 0, y1: 0, x2: heroWidth, y2: audioHeroHeight },
      colorStops: [
        { offset: 0, color: 'rgba(15, 23, 42, 0.98)' },
        { offset: 0.5, color: 'rgba(30, 41, 59, 0.95)' },
        { offset: 1, color: 'rgba(15, 23, 42, 0.92)' },
      ],
    });
    const audioPanel = new Rect({
      left: contentX,
      top: currentY,
      width: heroWidth,
      height: audioHeroHeight,
      rx: 48,
      ry: 48,
      fill: audioGradient,
      stroke: colorTheme.cardStroke,
      strokeWidth: 1.5,
      shadow: new Shadow({ color: colorTheme.surfaceShadow, blur: 45, offsetY: 22 }),
      selectable: false,
    });
    cardElements.push({ element: audioPanel, relativeTop: currentY });

    // Background decorative circles - subtle
    const circleGradient1 = new Gradient({
      type: 'radial',
      gradientUnits: 'pixels',
      coords: { x1: 100, y1: 100, x2: 100, y2: 100, r1: 0, r2: 180 },
      colorStops: [
        { offset: 0, color: `${colorTheme.accent}18` },
        { offset: 1, color: `${colorTheme.accent}00` },
      ],
    });
    const decorCircle1 = new Circle({
      left: contentX + heroWidth * 0.25,
      top: currentY + audioHeroHeight * 0.45,
      radius: 180,
      originX: 'center',
      originY: 'center',
      fill: circleGradient1,
      selectable: false,
    });
    cardElements.push({ element: decorCircle1, relativeTop: currentY + audioHeroHeight * 0.45 });

    const circleGradient2 = new Gradient({
      type: 'radial',
      gradientUnits: 'pixels',
      coords: { x1: 70, y1: 70, x2: 70, y2: 70, r1: 0, r2: 140 },
      colorStops: [
        { offset: 0, color: `${colorTheme.accentSecondary}15` },
        { offset: 1, color: `${colorTheme.accentSecondary}00` },
      ],
    });
    const decorCircle2 = new Circle({
      left: contentX + heroWidth * 0.75,
      top: currentY + audioHeroHeight * 0.65,
      radius: 140,
      originX: 'center',
      originY: 'center',
      fill: circleGradient2,
      selectable: false,
    });
    cardElements.push({ element: decorCircle2, relativeTop: currentY + audioHeroHeight * 0.65 });

    // Central play button - bubble style like footer buttons
    const playButtonSize = 140;
    const playButtonGradient = new Gradient({
      type: 'linear',
      gradientUnits: 'pixels',
      coords: { x1: 0, y1: 0, x2: playButtonSize, y2: playButtonSize },
      colorStops: [
        { offset: 0, color: `${colorTheme.accent}33` },
        { offset: 0.5, color: `${colorTheme.accent}22` },
        { offset: 1, color: `${colorTheme.accent}2a` },
      ],
    });
    const playButton = new Circle({
      left: contentX + heroWidth / 2,
      top: currentY + audioHeroHeight / 2 - 10,
      radius: playButtonSize / 2,
      originX: 'center',
      originY: 'center',
      fill: playButtonGradient,
      stroke: `${colorTheme.accent}55`,
      strokeWidth: 2,
      selectable: false,
    });
    cardElements.push({ element: playButton, relativeTop: currentY + audioHeroHeight / 2 - 10 });

    // Play triangle icon
    const playTriangle = new Textbox('▶', {
      left: contentX + heroWidth / 2 + 6,
      top: currentY + audioHeroHeight / 2 - 10,
      width: 60,
      fontSize: 52,
      fontFamily: 'Arial, sans-serif',
      fill: colorTheme.accent,
      originX: 'center',
      originY: 'center',
      selectable: false,
      textAlign: 'center',
    });
    cardElements.push({ element: playTriangle, relativeTop: currentY + audioHeroHeight / 2 - 10 });

    // Dynamic waveform visualization with musical peaks
    const barCount = 48;
    const barSpacing = 4;
    const totalBarsWidth = heroWidth - 80;
    const barWidth = (totalBarsWidth - (barCount - 1) * barSpacing) / barCount;
    const maxBarHeight = 75;
    // Waveform baseline is exactly at the bottom of the panel (same as audio with cover)
    const panelBottom = currentY + audioHeroHeight;
    const waveformBaseline = panelBottom;

    // Create dynamic peaks at different positions
    const peakPositions = [0.12, 0.28, 0.45, 0.62, 0.78, 0.92];
    
    for (let i = 0; i < barCount; i++) {
      const progress = i / barCount;
      
      // Calculate influence from nearest peaks
      let peakInfluence = 0;
      for (const peak of peakPositions) {
        const dist = Math.abs(progress - peak);
        peakInfluence = Math.max(peakInfluence, Math.max(0, 1 - dist * 5));
      }
      
      // Add variation for more organic feel
      const variation = Math.sin(i * 0.9) * 0.12 + Math.cos(i * 1.3) * 0.08;
      const barHeight = Math.max(12, (0.2 + peakInfluence * 0.8 + variation) * maxBarHeight);

      const barGradient = new Gradient({
        type: 'linear',
        gradientUnits: 'pixels',
        coords: { x1: 0, y1: barHeight, x2: 0, y2: 0 },
        colorStops: [
          { offset: 0, color: `${colorTheme.accent}55` },
          { offset: 0.5, color: colorTheme.accent },
          { offset: 1, color: colorTheme.accentSecondary },
        ],
      });

      const bar = new Rect({
        left: contentX + 40 + i * (barWidth + barSpacing),
        top: waveformBaseline - barHeight,
        width: barWidth,
        height: barHeight,
        rx: barWidth / 2,
        ry: barWidth / 2,
        fill: barGradient,
        selectable: false,
      });
      cardElements.push({ element: bar, relativeTop: waveformBaseline - barHeight });
    }

    currentY = waveformBaseline + 50;
  }
  // Regular post with image
  else if (post.imageUrl) {
    const postImage = await FabricImage.fromURL(post.imageUrl, { crossOrigin: 'anonymous' }, { selectable: false });

    const imgWidth = postImage.width ?? 1;
    const imgHeight = postImage.height ?? 1;

    const scaleX = heroWidth / imgWidth;
    const scaleY = heroHeight / imgHeight;
    const scale = Math.max(scaleX, scaleY);

    postImage.scaleX = scale;
    postImage.scaleY = scale;

    const clipRect = new Rect({
      width: heroWidth / scale,
      height: heroHeight / scale,
      rx: 48 / scale,
      ry: 48 / scale,
      originX: 'center',
      originY: 'center',
    });

    postImage.set({
      left: contentX + heroWidth / 2,
      top: currentY + heroHeight / 2,
      originX: 'center',
      originY: 'center',
      clipPath: clipRect,
      shadow: new Shadow({
        color: colorTheme.surfaceShadow,
        blur: 50,
        offsetY: 25,
      }),
    });
    postImage.setCoords();
    cardElements.push({ element: postImage, relativeTop: currentY + heroHeight / 2 });

    // Decorative accent overlay at bottom of image
    const imageOverlayGradient = new Gradient({
      type: 'linear',
      gradientUnits: 'pixels',
      coords: { x1: 0, y1: 0, x2: heroWidth, y2: heroHeight * 0.5 },
      colorStops: [
        { offset: 0, color: `${colorTheme.accent}00` },
        { offset: 0.7, color: `${colorTheme.accent}15` },
        { offset: 1, color: `${colorTheme.accent}25` },
      ],
    });
    const imageAccentOverlay = new Rect({
      left: contentX,
      top: currentY + heroHeight * 0.5,
      width: heroWidth,
      height: heroHeight * 0.5,
      rx: 48,
      ry: 48,
      fill: imageOverlayGradient,
      selectable: false,
    });
    cardElements.push({ element: imageAccentOverlay, relativeTop: currentY + heroHeight * 0.5 });

    // Small accent dot at corner
    const accentDot = new Circle({
      left: contentX + heroWidth - 50,
      top: currentY + 50,
      radius: 8,
      originX: 'center',
      originY: 'center',
      fill: colorTheme.accent,
      shadow: new Shadow({ color: colorTheme.accentGlow, blur: 20, offsetX: 0, offsetY: 0 }),
      selectable: false,
    });
    cardElements.push({ element: accentDot, relativeTop: currentY + 50 });

    currentY += heroHeight + 60;
  } else {
    const fallbackGradient = new Gradient({
      type: 'linear',
      gradientUnits: 'pixels',
      coords: { x1: 0, y1: 0, x2: heroWidth, y2: heroHeight },
      colorStops: [
        { offset: 0, color: 'rgba(15, 23, 42, 0.95)' },
        { offset: 0.5, color: 'rgba(30, 41, 59, 0.9)' },
        { offset: 1, color: 'rgba(15, 23, 42, 0.85)' },
      ],
    });
    const fallbackPanel = new Rect({
      left: contentX,
      top: currentY,
      width: heroWidth,
      height: heroHeight,
      rx: 48,
      ry: 48,
      fill: fallbackGradient,
      stroke: colorTheme.cardStroke,
      strokeWidth: 1.5,
      shadow: new Shadow({ color: colorTheme.surfaceShadow, blur: 45, offsetY: 22 }),
      selectable: false,
    });
    cardElements.push({ element: fallbackPanel, relativeTop: currentY });

    const quoteSymbol = new Textbox('"', {
      top: currentY + 30,
      left: contentX + 50,
      width: 200,
      fontSize: 180,
      fontFamily: 'Georgia, serif',
      fontWeight: '700',
      fill: colorTheme.accent,
      opacity: 0.25,
      selectable: false,
      textAlign: 'left',
    });
    cardElements.push({ element: quoteSymbol, relativeTop: currentY + 20 });

    const contentText = post.content?.trim() || 'Shared on Soodora';
    const fallbackContent = createAutoSizedTextbox(createExcerptCopy(contentText, 200), {
      top: currentY + heroHeight * 0.25,
      left: contentX + 70,
      maxWidth: heroWidth - 140,
      maxHeight: heroHeight * 0.55,
      maxFontSize: 44,
      minFontSize: 28,
      fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
      fontWeight: '500',
      fontStyle: 'italic',
      fill: '#f1f5f9',
      selectable: false,
      lineHeight: 1.55,
      textAlign: 'left',
      truncateOverflow: true,
    });
    fallbackContent.initDimensions();
    cardElements.push({ element: fallbackContent, relativeTop: currentY + heroHeight * 0.25 });

    const accentLineGradient = new Gradient({
      type: 'linear',
      gradientUnits: 'pixels',
      coords: { x1: 0, y1: 0, x2: heroWidth * 0.4, y2: 0 },
      colorStops: [
        { offset: 0, color: colorTheme.accent },
        { offset: 1, color: colorTheme.accentSecondary },
      ],
    });
    const accentLine = new Rect({
      left: contentX + 70,
      top: currentY + heroHeight - 70,
      width: heroWidth * 0.4,
      height: 4,
      rx: 2,
      ry: 2,
      fill: accentLineGradient,
      selectable: false,
    });
    cardElements.push({ element: accentLine, relativeTop: currentY + heroHeight - 70 });

    currentY += heroHeight + 60;
  }

  // Show post body for posts with image or audio posts with cover
  const showBodyContent = post.imageUrl || (isAudioPost && post.imageUrl);
  const postBodyCopy = showBodyContent ? createExcerptCopy(post.content ?? '', 140) : '';
  if (postBodyCopy) {
    const postContent = createAutoSizedTextbox(postBodyCopy, {
      top: currentY,
      left: contentX,
      maxWidth: contentWidth,
      maxHeight: 420,
      maxFontSize: 42,
      minFontSize: 30,
      fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
      fontWeight: '400',
      fill: colorTheme.secondary,
      selectable: false,
      lineHeight: 1.5,
      textAlign: 'left',
      truncateOverflow: true,
    });
    postContent.initDimensions();
    cardElements.push({ element: postContent, relativeTop: currentY });
    currentY += postContent.getScaledHeight() + 48;
  }

  const footerLine = new Rect({
    left: contentX,
    top: currentY,
    width: contentWidth,
    height: 1.5,
    fill: colorTheme.cardStroke,
    selectable: false,
  });
  cardElements.push({ element: footerLine, relativeTop: currentY });

  const exploreWidth = Math.min(260, contentWidth * 0.4);
  const exploreHeight = 56;
  const buttonOffset = (72 - exploreHeight) / 2;
  
  const exploreGradient = new Gradient({
    type: 'linear',
    gradientUnits: 'pixels',
    coords: { x1: 0, y1: 0, x2: exploreWidth, y2: exploreHeight },
    colorStops: [
      { offset: 0, color: `${colorTheme.accent}22` },
      { offset: 0.5, color: `${colorTheme.accent}11` },
      { offset: 1, color: `${colorTheme.accent}1a` },
    ],
  });
  const exploreButton = new Rect({
    left: contentX,
    top: currentY + 48 + buttonOffset,
    width: exploreWidth,
    height: exploreHeight,
    rx: exploreHeight / 2,
    ry: exploreHeight / 2,
    fill: exploreGradient,
    stroke: `${colorTheme.accent}40`,
    strokeWidth: 1.5,
    shadow: new Shadow({ color: 'rgba(0, 0, 0, 0.15)', blur: 25, offsetY: 8 }),
    selectable: false,
  });
  cardElements.push({ element: exploreButton, relativeTop: currentY + 48 + buttonOffset });

  const footerButtonText = isAudioPost ? 'LISTEN' : 'EXPLORE';
  const watermark = createAutoSizedTextbox(footerButtonText, {
    top: currentY + 48 + buttonOffset + (exploreHeight - 24) / 2,
    left: contentX,
    maxWidth: exploreWidth,
    maxHeight: exploreHeight - 20,
    maxFontSize: 26,
    minFontSize: 20,
    fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
    fontWeight: '800',
    fill: colorTheme.accent,
    selectable: false,
    textAlign: 'center',
    charSpacing: 250,
  });
  cardElements.push({ element: watermark, relativeTop: currentY + 48 + buttonOffset + (exploreHeight - 24) / 2 });

  const postLogoWidth = Math.min(280, contentWidth * 0.4);
  const postLogoHeight = 56;
  const ctaLeft = contentX + contentWidth - postLogoWidth;
  const logoPath = theme === 'white' ? '/assets/images/png/banner-dark.png' : '/assets/images/png/banner-light.png';
  const logoImage = await FabricImage.fromURL(logoPath, { crossOrigin: 'anonymous' }, { selectable: false });
  
  const imgWidth = logoImage.width ?? 1;
  const imgHeight = logoImage.height ?? 1;
  const scale = Math.min(postLogoWidth / imgWidth, (postLogoHeight * 0.8) / imgHeight);
  
  logoImage.scale(scale);
  logoImage.set({
    left: ctaLeft + postLogoWidth / 2,
    top: currentY + 48 + buttonOffset + postLogoHeight / 2,
    originX: 'center',
    originY: 'center',
  });
  logoImage.setCoords();
  cardElements.push({ element: logoImage, relativeTop: currentY + 48 + buttonOffset + postLogoHeight / 2 });

  const footerBottom = currentY + 56 + (watermark.height ?? 40) + 110;
  const logoBottom = currentY + 36 + postLogoHeight + 110;
  const cardContentHeight = Math.max(footerBottom, logoBottom) - tempCardTop;
  const finalCardHeight = cardContentHeight;

  const centeredCardTop = (canvasHeight - finalCardHeight) / 2;
  const verticalOffset = centeredCardTop - tempCardTop;

  const card = createGlassmorphicCard(cardLeft, centeredCardTop, cardWidth, finalCardHeight, colorTheme, 56, theme);
  canvas.add(card);

  for (const { element, relativeTop } of cardElements) {
    element.set({ top: relativeTop + verticalOffset });
    element.setCoords();
    canvas.add(element as Parameters<typeof canvas.add>[0]);
  }

  // Shift everything so the card starts at (0,0) and crop the canvas to the card bounds.
  const offsetX = cardLeft;
  const offsetY = centeredCardTop;
  canvas.getObjects().forEach((obj) => {
    const left = obj.left ?? 0;
    const top = obj.top ?? 0;
    obj.set({ left: left - offsetX, top: top - offsetY });
    obj.setCoords();
  });

  canvas.setWidth(cardWidth);
  canvas.setHeight(finalCardHeight);
  canvas.backgroundColor = 'rgba(0,0,0,0)';
  canvas.renderAll();

  return canvas;
}

async function createProductCanvas(
  product: Product,
  theme: ExportTheme,
  customColors?: CustomColors
): Promise<StaticCanvas> {
  const colorTheme = retrieveColorTheme(theme, customColors);
  const canvas = new StaticCanvas(undefined, {
    width: 1080,
    height: 1920,
    backgroundColor: 'rgba(0,0,0,0)',
  });

  const cardTop = 200;
  const cardLeft = 80;
  const cardWidth = canvas.getWidth() - 160;
  const maxCardHeight = canvas.getHeight() - cardTop - 120;
  const baseCardHeight = Math.min(maxCardHeight, 1320);
  const card = createGlassmorphicCard(cardLeft, cardTop, cardWidth, baseCardHeight, colorTheme, 56, theme);
  canvas.add(card);

  const horizontalPadding = 96;
  const contentX = cardLeft + horizontalPadding;
  const contentWidth = cardWidth - horizontalPadding * 2;
  let currentY = cardTop + 90;

  const heroWidth = contentWidth;
  const heroHeight = heroWidth * 0.58;
  if (product.imageUrl) {
    const productImage = await FabricImage.fromURL(
      product.imageUrl,
      { crossOrigin: 'anonymous' },
      { selectable: false }
    );

    const imgWidth = productImage.width ?? 1;
    const imgHeight = productImage.height ?? 1;

    const scaleX = heroWidth / imgWidth;
    const scaleY = heroHeight / imgHeight;
    const scale = Math.max(scaleX, scaleY);

    productImage.scaleX = scale;
    productImage.scaleY = scale;

    const clipRect = new Rect({
      width: heroWidth / scale,
      height: heroHeight / scale,
      rx: 52 / scale,
      ry: 52 / scale,
      originX: 'center',
      originY: 'center',
    });

    productImage.set({
      left: contentX + heroWidth / 2,
      top: currentY + heroHeight / 2,
      originX: 'center',
      originY: 'center',
      clipPath: clipRect,
      shadow: new Shadow({ color: colorTheme.surfaceShadow, blur: 60, offsetY: 30 }),
    });

    productImage.setCoords();
    canvas.add(productImage);
  } else {
    const fallbackGradient = new Gradient({
      type: 'linear',
      gradientUnits: 'pixels',
      coords: { x1: 0, y1: 0, x2: heroWidth, y2: heroHeight },
      colorStops: [
        { offset: 0, color: 'rgba(15, 23, 42, 0.95)' },
        { offset: 0.5, color: 'rgba(30, 41, 59, 0.9)' },
        { offset: 1, color: 'rgba(15, 23, 42, 0.85)' },
      ],
    });
    const fallbackPanel = new Rect({
      left: contentX,
      top: currentY,
      width: heroWidth,
      height: heroHeight,
      rx: 52,
      ry: 52,
      fill: fallbackGradient,
      stroke: colorTheme.cardStroke,
      strokeWidth: 1.5,
      shadow: new Shadow({ color: colorTheme.surfaceShadow, blur: 55, offsetY: 25 }),
      selectable: false,
    });
    canvas.add(fallbackPanel);

    const priceTagWidth = Math.min(280, heroWidth * 0.4);
    const priceTagGradient = new Gradient({
      type: 'linear',
      gradientUnits: 'pixels',
      coords: { x1: 0, y1: 0, x2: priceTagWidth, y2: 0 },
      colorStops: [
        { offset: 0, color: `${colorTheme.accent}33` },
        { offset: 1, color: `${colorTheme.accentSecondary}22` },
      ],
    });
    const priceTag = new Rect({
      left: contentX + 60,
      top: currentY + 50,
      width: priceTagWidth,
      height: 64,
      rx: 32,
      ry: 32,
      fill: priceTagGradient,
      stroke: `${colorTheme.accent}40`,
      strokeWidth: 1,
      selectable: false,
    });
    canvas.add(priceTag);

    const priceTagText = createAutoSizedTextbox(`${product.price.amount}${product.price.symbol}`, {
      top: currentY + 62,
      left: contentX + 60,
      maxWidth: priceTagWidth,
      maxHeight: 44,
      maxFontSize: 36,
      minFontSize: 24,
      fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
      fontWeight: '700',
      fill: '#f1f5f9',
      selectable: false,
      textAlign: 'center',
      splitByGrapheme: false,
      truncateOverflow: true,
    });
    canvas.add(priceTagText);

    const descriptionText = product.description?.trim() || 'Premium product curated for the Soodora marketplace.';
    const fallbackDescription = createAutoSizedTextbox(createExcerptCopy(descriptionText, 240), {
      top: currentY + heroHeight * 0.32,
      left: contentX + 60,
      maxWidth: heroWidth - 120,
      maxHeight: heroHeight * 0.5,
      maxFontSize: 40,
      minFontSize: 26,
      fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
      fontWeight: '500',
      fontStyle: 'italic',
      fill: '#e2e8f0',
      selectable: false,
      lineHeight: 1.5,
      textAlign: 'left',
      truncateOverflow: true,
    });
    canvas.add(fallbackDescription);

    const accentLineGradient = new Gradient({
      type: 'linear',
      gradientUnits: 'pixels',
      coords: { x1: 0, y1: 0, x2: heroWidth * 0.4, y2: 0 },
      colorStops: [
        { offset: 0, color: colorTheme.accent },
        { offset: 1, color: colorTheme.accentSecondary },
      ],
    });
    const accentLine = new Rect({
      left: contentX + 60,
      top: currentY + heroHeight - 60,
      width: heroWidth * 0.4,
      height: 4,
      rx: 2,
      ry: 2,
      fill: accentLineGradient,
      selectable: false,
    });
    canvas.add(accentLine);
  }

  const heroToTitleSpacing = 60;
  currentY += heroHeight + heroToTitleSpacing;

  const productTitleMaxHeight = Math.min(240, heroHeight * 0.55);
  const displayProductName = createExcerptCopy(product.name?.toUpperCase() ?? 'SOODORA EXCLUSIVE', 80);
  const productName = createAutoSizedTextbox(displayProductName, {
    top: currentY,
    left: contentX,
    maxWidth: contentWidth,
    maxHeight: productTitleMaxHeight,
    maxFontSize: 88,
    minFontSize: 36,
    fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
    fontWeight: '900',
    fill: colorTheme.primary,
    selectable: false,
    lineHeight: 1.05,
    textAlign: 'left',
    truncateOverflow: true,
    splitByGrapheme: false,
    breakWords: false,
  });
  canvas.add(productName);
  productName.initDimensions();
  currentY += productName.getScaledHeight() + 24;

  const productDescriptionCopy = product.imageUrl ? createExcerptCopy(product.description ?? '', 110) : '';
  if (productDescriptionCopy) {
    const productDescription = createAutoSizedTextbox(productDescriptionCopy, {
      top: currentY,
      left: contentX,
      maxWidth: contentWidth,
      maxHeight: 360,
      maxFontSize: 40,
      minFontSize: 28,
      fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
      fontWeight: '400',
      fill: colorTheme.secondary,
      selectable: false,
      lineHeight: 1.5,
      textAlign: 'left',
      truncateOverflow: true,
    });
    canvas.add(productDescription);
    productDescription.initDimensions();
    currentY += productDescription.getScaledHeight() + 36;
  }

  const priceTop = currentY;
  const ctaHeight = 110;
  const ctaWidth = Math.min(360, contentWidth / 2);
  const ctaLeft = contentX + contentWidth - ctaWidth;
  const priceGap = 72;
  const priceAreaWidth = Math.max(ctaLeft - contentX - priceGap, contentWidth * 0.42);
  const priceBlock = createAutoSizedTextbox(`${product.price.amount}${product.price.symbol}`, {
    top: priceTop,
    left: contentX,
    maxWidth: priceAreaWidth,
    maxHeight: 150,
    maxFontSize: 140,
    minFontSize: 70,
    fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
    fontWeight: '900',
    fill: colorTheme.accent,
    selectable: false,
    textAlign: 'left',
    splitByGrapheme: false,
    enforceSingleLine: true,
  });
  canvas.add(priceBlock);
  priceBlock.initDimensions();
  const priceVerticalOffset = Math.max(0, (ctaHeight - priceBlock.getScaledHeight()) / 2);
  priceBlock.set({ top: priceTop + priceVerticalOffset });
  priceBlock.setCoords();

  const ctaGradient = new Gradient({
    type: 'linear',
    gradientUnits: 'pixels',
    coords: { x1: 0, y1: 0, x2: ctaWidth, y2: ctaHeight },
    colorStops: [
      { offset: 0, color: `${colorTheme.accent}22` },
      { offset: 0.5, color: `${colorTheme.accent}11` },
      { offset: 1, color: `${colorTheme.accent}1a` },
    ],
  });
  const buyButton = new Rect({
    left: ctaLeft,
    top: priceTop + 12,
    width: ctaWidth,
    height: ctaHeight,
    rx: ctaHeight / 2,
    ry: ctaHeight / 2,
    fill: ctaGradient,
    stroke: `${colorTheme.accent}40`,
    strokeWidth: 1.5,
    shadow: new Shadow({ color: 'rgba(0, 0, 0, 0.15)', blur: 25, offsetY: 8 }),
    selectable: false,
  });
  canvas.add(buyButton);

  const buyText = createAutoSizedTextbox('SHOP ON SOODORA', {
    top: priceTop + 40,
    left: ctaLeft,
    maxWidth: ctaWidth,
    maxHeight: ctaHeight - 24,
    maxFontSize: 34,
    minFontSize: 24,
    fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
    fontWeight: '600',
    fill: colorTheme.accent,
    selectable: false,
    textAlign: 'center',
    charSpacing: 120,
    splitByGrapheme: false,
  });
  buyText.set({ top: (buyButton.top ?? 0) + (ctaHeight - (buyText.height ?? 0)) / 2 });
  buyText.setCoords();
  canvas.add(buyText);
  buyText.initDimensions();

  const priceRowHeight = Math.max(priceBlock.getScaledHeight(), ctaHeight, buyText.getScaledHeight());
  currentY = priceTop + priceRowHeight + 90;

  const vendorAvatar = await createCircularAvatar(
    product.vendor.avatarUrl ?? '/assets/images/svg/avatar.svg',
    contentX,
    currentY,
    110,
    colorTheme.accent
  );
  canvas.add(vendorAvatar);

  const vendorHandle = product.vendor.username?.trim()
    ? `@${product.vendor.username.trim()}`
    : product.vendor.slug?.trim()
      ? `@${product.vendor.slug.trim()}`
      : '@soodora';

  const vendorUsername = createAutoSizedTextbox(vendorHandle, {
    top: currentY + 10,
    left: contentX + 140,
    maxWidth: contentWidth - 140,
    maxHeight: 90,
    maxFontSize: 52,
    minFontSize: 40,
    fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
    fontWeight: '700',
    fill: colorTheme.primary,
    selectable: false,
    textAlign: 'left',
    truncateOverflow: true,
  });
  canvas.add(vendorUsername);

  const vendorSubtitle = createAutoSizedTextbox('Verified Soodora Vendor', {
    top: (vendorUsername.top ?? 0) + (vendorUsername.height ?? 0) + 8,
    left: contentX + 140,
    maxWidth: contentWidth - 140,
    maxHeight: 70,
    maxFontSize: 36,
    minFontSize: 28,
    fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
    fontWeight: '500',
    fill: colorTheme.secondary,
    selectable: false,
    textAlign: 'left',
    truncateOverflow: true,
  });
  canvas.add(vendorSubtitle);

  const vendorSubtitleBottom = (vendorSubtitle.top ?? 0) + (vendorSubtitle.height ?? 0);

  const totalGap = 80;
  const halfGap = totalGap / 2;

  const footerLineTop = vendorSubtitleBottom + halfGap;
  const marketplaceTop = footerLineTop + halfGap;

  const footerLine = new Rect({
    left: contentX,
    top: footerLineTop,
    width: contentWidth,
    height: 1.5,
    fill: colorTheme.cardStroke,
    selectable: false,
  });
  canvas.add(footerLine);

  const marketplaceWidth = Math.min(340, contentWidth * 0.48);
  const marketplaceHeight = 56;
  const buttonOffset = (72 - marketplaceHeight) / 2;
  
  const marketplaceGradient = new Gradient({
    type: 'linear',
    gradientUnits: 'pixels',
    coords: { x1: 0, y1: 0, x2: marketplaceWidth, y2: marketplaceHeight },
    colorStops: [
      { offset: 0, color: `${colorTheme.accent}22` },
      { offset: 0.5, color: `${colorTheme.accent}11` },
      { offset: 1, color: `${colorTheme.accent}1a` },
    ],
  });
  const marketplaceButton = new Rect({
    left: contentX,
    top: marketplaceTop + buttonOffset,
    width: marketplaceWidth,
    height: marketplaceHeight,
    rx: marketplaceHeight / 2,
    ry: marketplaceHeight / 2,
    fill: marketplaceGradient,
    stroke: `${colorTheme.accent}40`,
    strokeWidth: 1.5,
    shadow: new Shadow({ color: 'rgba(0, 0, 0, 0.15)', blur: 25, offsetY: 8 }),
    selectable: false,
  });
  canvas.add(marketplaceButton);

  const watermark = createAutoSizedTextbox('MARKETPLACE', {
    top: marketplaceTop + buttonOffset + (marketplaceHeight - 24) / 2,
    left: contentX,
    maxWidth: marketplaceWidth,
    maxHeight: marketplaceHeight - 20,
    maxFontSize: 26,
    minFontSize: 20,
    fontFamily: 'Overpass, -apple-system, SF Pro Display, sans-serif',
    fontWeight: '800',
    fill: colorTheme.accent,
    selectable: false,
    textAlign: 'center',
    charSpacing: 200,
  });
  canvas.add(watermark);
  watermark.initDimensions();
  const watermarkHeight = watermark.height ?? 40;

  const productLogoWidth = Math.min(280, contentWidth * 0.4);
  const productLogoHeight = 56;
  const logoLeft = contentX + contentWidth - productLogoWidth;
  const logoPath = theme === 'white' ? '/assets/images/png/banner-dark.png' : '/assets/images/png/banner-light.png';
  const logoImage = await FabricImage.fromURL(logoPath, { crossOrigin: 'anonymous' }, { selectable: false });
  
  const imgWidth = logoImage.width ?? 1;
  const imgHeight = logoImage.height ?? 1;
  const scale = Math.min(productLogoWidth / imgWidth, (productLogoHeight * 0.8) / imgHeight);
  
  logoImage.scale(scale);
  logoImage.set({
    left: logoLeft + productLogoWidth / 2,
    top: marketplaceTop + buttonOffset + productLogoHeight / 2,
    originX: 'center',
    originY: 'center',
  });
  logoImage.setCoords();
  canvas.add(logoImage);

  const contentBottom = marketplaceTop + Math.max(watermarkHeight, productLogoHeight) + 90;
  const cardContentHeight = contentBottom - cardTop;
  const finalCardHeight = cardContentHeight;

  const canvasHeight = canvas.getHeight();
  const centeredCardTop = (canvasHeight - finalCardHeight) / 2;
  const verticalOffset = centeredCardTop - cardTop;

  const allObjects = canvas.getObjects();
  allObjects.forEach((obj) => {
    if (obj === card) {
      obj.set({ top: centeredCardTop });
      obj.setCoords();
    } else {
      const objTop = obj.top ?? 0;
      if (objTop >= cardTop - 10) {
        obj.set({ top: objTop + verticalOffset });
        obj.setCoords();
      }
    }
  });

  updateCardAppearance(card, finalCardHeight, colorTheme);

  // Shift everything so the card starts at (0,0) and crop the canvas to the card bounds.
  const offsetX = cardLeft;
  const offsetY = centeredCardTop;
  canvas.getObjects().forEach((obj) => {
    const left = obj.left ?? 0;
    const top = obj.top ?? 0;
    obj.set({ left: left - offsetX, top: top - offsetY });
    obj.setCoords();
  });

  canvas.setWidth(cardWidth);
  canvas.setHeight(finalCardHeight);
  canvas.backgroundColor = 'rgba(0,0,0,0)';
  canvas.renderAll();

  return canvas;
}

function downloadCanvas(canvas: StaticCanvas): void {
  const imageData: string = canvas.toDataURL();
  const anchor: HTMLAnchorElement = document.createElement('a');

  anchor.href = imageData;
  anchor.download = `Export - ${new Date().toDateString()}`;

  document.body.appendChild(anchor);
  anchor.click();
  document.body.removeChild(anchor);
}

export { createPostCanvas, createProductCanvas, downloadCanvas };
export type { ExportTheme, CustomColors };
