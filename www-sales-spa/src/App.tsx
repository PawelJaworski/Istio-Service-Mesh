import React from "react"
import './App.css';
import {Provider} from "react-redux"
import OfferPage from "./view/offer/OfferPage";
import {newAppStore} from "./view/app/store/AppStore";

const store = newAppStore()
const App = () => {
    return (<>
            <Provider store={store}>
                <OfferPage/>
            </Provider>
        </>
    )
}
export default App
