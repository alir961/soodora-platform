import { StaticCanvas, FabricImage, Textbox } from 'fabric';
import { Post } from '@/entity/Post';
import { Product } from '@/entity/Product';

type ExportTheme = 'black' | 'white' | 'transparent';

interface ColorTheme {
  background: string;
  primary: string;
  secondary: string;
  banner: string;
}

function retrieveColorTheme(theme: ExportTheme): ColorTheme {
  switch (theme) {
    case 'black':
      return {
        background: '#000000ff',
        primary: '#ffffffff',
        secondary: '#ddddddff',
        banner: 'banner-light.png',
      } as ColorTheme;
    case 'white':
      return {
        background: '#ffffffff',
        primary: '#000000ff',
        secondary: '#222222ff',
        banner: 'banner-dark.png',
      } as ColorTheme;
    case 'transparent':
      return {
        background: '#ffffff00',
        primary: '#000000ff',
        secondary: '#222222ff',
        banner: 'banner-dark.png',
      } as ColorTheme;
  }
}

async function retrieveSoodoraBanner(colorTheme: ColorTheme): Promise<FabricImage> {
  const soodoraBanner = await FabricImage.fromURL(
    `/assets/images/png/${colorTheme.banner}`,
    {},
    {
      top: 50,
      left: 50,
      selectable: false,
    }
  );

  soodoraBanner.scaleToWidth(980);
  return soodoraBanner;
}

async function createPostCanvas(post: Post, theme: ExportTheme): Promise<StaticCanvas> {
  let currentY = 50;
  const colorTheme = retrieveColorTheme(theme);
  const canvas = new StaticCanvas(undefined, { width: 1080, height: 1920, backgroundColor: colorTheme.background });
  const soodoraBanner = await retrieveSoodoraBanner(colorTheme);

  currentY += soodoraBanner.getScaledHeight() + 100;

  const usernameSize = 80;
  const avatarWidth = 150;
  const authorAvatar = await FabricImage.fromURL(
    post.owner.avatarUrl ?? '/assets/images/svg/avatar.svg',
    { crossOrigin: 'anonymous' },
    {
      top: currentY,
      left: 50,
      selectable: false,
    }
  );

  authorAvatar.scaleToWidth(avatarWidth);

  const authorUsername = new Textbox(post.owner.username, {
    top: currentY + avatarWidth - usernameSize,
    left: 75 + authorAvatar.getScaledWidth(),
    width: 980 - authorAvatar.getScaledWidth(),
    fontSize: usernameSize,
    fontFamily: 'sans-serif',
    fontWeight: 'bold',
    fill: colorTheme.secondary,
    selectable: false,
  });

  currentY += authorAvatar.getScaledHeight() + 50;

  const postTitle = new Textbox(post.title, {
    top: currentY,
    left: 50,
    width: 980,
    fontSize: 80,
    fontFamily: 'sans-serif',
    fontWeight: 'bold',
    fill: colorTheme.primary,
    selectable: false,
  });

  currentY += postTitle.height + 30;

  const postContent = new Textbox(post.content, {
    top: currentY,
    left: 50,
    width: 980,
    fontSize: 36,
    fontFamily: 'sans-serif',
    fill: colorTheme.secondary,
    selectable: false,
  });

  currentY += postContent.height + 50;

  const postImage = await FabricImage.fromURL(
    post.imageUrl,
    { crossOrigin: 'anonymous' },
    {
      top: currentY,
      left: 50,
      selectable: false,
    }
  );

  postImage.scaleToWidth(980);

  canvas.add(soodoraBanner);
  canvas.add(authorAvatar);
  canvas.add(authorUsername);
  canvas.add(postTitle);
  canvas.add(postContent);
  canvas.add(postImage);

  return canvas;
}

async function createProductCanvas(product: Product, theme: ExportTheme): Promise<StaticCanvas> {
  let currentY = 50;
  const colorTheme = retrieveColorTheme(theme);
  const canvas = new StaticCanvas(undefined, { width: 1080, height: 1920, backgroundColor: colorTheme.background });
  const soodoraBanner = await retrieveSoodoraBanner(colorTheme);

  currentY += soodoraBanner.getScaledHeight() + 100;

  const usernameSize = 80;
  const avatarWidth = 150;
  const authorAvatar = await FabricImage.fromURL(
    product.vendor.avatarUrl ?? '/assets/images/svg/avatar.svg',
    { crossOrigin: 'anonymous' },
    {
      top: currentY,
      left: 50,
      selectable: false,
    }
  );

  authorAvatar.scaleToWidth(avatarWidth);

  const authorUsername = new Textbox(product.vendor.username, {
    top: currentY + avatarWidth - usernameSize,
    left: 75 + authorAvatar.getScaledWidth(),
    width: 980 - authorAvatar.getScaledWidth(),
    fontSize: usernameSize,
    fontFamily: 'sans-serif',
    fontWeight: 'bold',
    fill: colorTheme.secondary,
    selectable: false,
  });

  currentY += authorAvatar.getScaledHeight() + 50;

  const productTitle = new Textbox(`${product.name} - ${product.price}€`, {
    top: currentY,
    left: 50,
    width: 980,
    fontSize: 80,
    fontFamily: 'sans-serif',
    fontWeight: 'bold',
    fill: colorTheme.primary,
    selectable: false,
  });

  currentY += productTitle.height + 30;

  const productContent = new Textbox(product.description, {
    top: currentY,
    left: 50,
    width: 980,
    fontSize: 36,
    fontFamily: 'sans-serif',
    fill: colorTheme.secondary,
    selectable: false,
  });

  currentY += productContent.height + 50;

  const productImage = await FabricImage.fromURL(
    product.imageUrl,
    { crossOrigin: 'anonymous' },
    {
      top: currentY,
      left: 50,
      selectable: false,
    }
  );

  productImage.scaleToWidth(980);

  canvas.add(soodoraBanner);
  canvas.add(authorAvatar);
  canvas.add(authorUsername);
  canvas.add(productTitle);
  canvas.add(productContent);
  canvas.add(productImage);

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
export type { ExportTheme };
