import * as React from 'react';
import { render, unmountComponentAtNode } from 'react-dom';
import {WComponent} from "./js/WComponent";
import {OfferForm} from "./ts/view/OfferForm";
import {ErrorMessage, ErrorMessagePropTypes} from "./ts/view/common/Messages";

class OfferFormComponent extends WComponent {
  constructor() {
    super();
  }
  mount() {
    const propTypes = OfferForm.propTypes ? OfferForm.propTypes : {};
    const events = OfferForm.propTypes ? OfferForm.propTypes : {};
    const props = {
      ...this.getProps(this.attributes, propTypes),
      ...this.getEvents(events),
      children: this.parseHtmlToReact(this.innerHTML)
    };
    render(<OfferForm {...props} />, this);
  }
}

class ErrorMessageComponent extends WComponent {
  constructor() {
    super();
  }
  mount() {
    const propTypes = ErrorMessagePropTypes;
    const events = {};
    const props = {
      ...this.getProps(this.attributes, propTypes),
      ...this.getEvents(events),
      children: this.parseHtmlToReact(this.innerHTML)
    };
    render(<ErrorMessage {...props} />, this);
  }
}

customElements.define('offer-form', OfferFormComponent)
customElements.define('offer-error', ErrorMessageComponent);