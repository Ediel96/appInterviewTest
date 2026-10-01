import React from 'react';
import {fireEvent, render} from '@testing-library/react-native';
import {EmptyState} from '../EmptyState';
import {ErrorView} from '../ErrorView';
import {LoadingView} from '../LoadingView';

describe('feedback views', () => {
  it('shows the default loading message', async () => {
    const {getByLabelText, getByText} = await render(<LoadingView />);

    expect(getByLabelText('Loading...')).toBeTruthy();
    expect(getByText('Loading...')).toBeTruthy();
  });

  it('shows an error and invokes retry', async () => {
    const onRetry = jest.fn();
    const {getByRole, getByText} = await render(
      <ErrorView message="Network error" onRetry={onRetry} />,
    );

    expect(getByRole('alert')).toBeTruthy();
    expect(getByText('Network error')).toBeTruthy();

    await fireEvent.press(getByRole('button', {name: 'Retry'}));
    expect(onRetry).toHaveBeenCalledTimes(1);
  });

  it('does not render retry when no callback is supplied', async () => {
    const {queryByRole} = await render(<ErrorView message="Fatal error" />);

    expect(queryByRole('button', {name: 'Retry'})).toBeNull();
  });

  it('renders an empty state with an optional description', async () => {
    const {getByText} = await render(
      <EmptyState title="No products" description="Try again later" />,
    );

    expect(getByText('No products')).toBeTruthy();
    expect(getByText('Try again later')).toBeTruthy();
  });

  it('renders an empty state without a description', async () => {
    const {getByText, queryByText} = await render(
      <EmptyState title="No favorites" />,
    );

    expect(getByText('No favorites')).toBeTruthy();
    expect(queryByText('Try again later')).toBeNull();
  });
});
