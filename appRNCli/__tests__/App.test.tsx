import React from 'react';
import {render} from '@testing-library/react-native';
import App from '../App';

jest.mock('../src/navigation/RootNavigator', () => {
  const ReactModule = require('react');
  const {View} = require('react-native');

  return {
    RootNavigator: () =>
      ReactModule.createElement(View, {testID: 'root-navigator'}),
  };
});

jest.mock('react-native-safe-area-context', () => {
  const ReactModule = require('react');

  return {
    SafeAreaProvider: ({children}: {children: React.ReactNode}) =>
      ReactModule.createElement(ReactModule.Fragment, null, children),
  };
});

describe('App', () => {
  it('renders the application root', async () => {
    const {getByTestId} = await render(<App />);

    expect(getByTestId('root-navigator')).toBeTruthy();
  });
});
