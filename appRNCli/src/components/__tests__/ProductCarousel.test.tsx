import React from 'react';
import {fireEvent, render} from '@testing-library/react-native';
import {ProductCarousel} from '../ProductCarousel';

describe('ProductCarousel', () => {
  it('shows an empty state when there are no images', async () => {
    const {getByText} = await render(<ProductCarousel images={[]} />);

    expect(getByText('No images available')).toBeTruthy();
  });

  it('renders images, pagination dots and handles horizontal scrolling', async () => {
    const {container, getAllByLabelText, getByLabelText} = await render(
      <ProductCarousel
        images={[
          'https://example.com/image-1.jpg',
          'https://example.com/image-2.jpg',
        ]}
      />,
    );

    expect(getAllByLabelText('Product image')).toHaveLength(2);
    expect(getByLabelText('Image 1 of 2')).toBeTruthy();
    expect(getByLabelText('Image 2 of 2')).toBeTruthy();

    const scrollable = container.queryAll(
      instance => typeof instance.props.onScroll === 'function',
    )[0];
    await fireEvent.scroll(scrollable, {
      nativeEvent: {contentOffset: {x: 0}},
    });

    expect(scrollable).toBeTruthy();
  });
});
