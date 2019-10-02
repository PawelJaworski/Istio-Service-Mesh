import {createEpicMiddleware} from "redux-observable";
import {applyMiddleware, combineReducers, createStore, Reducer} from "redux";
import {ApplicationState, OfferPageState} from "./AppState";
import {offerReducer} from "../../offer/store/OfferReducer";
import {appEpic} from "./AppEpic";

const appReducers : Reducer<ApplicationState> = combineReducers<ApplicationState>({
    offerPage: offerReducer()
})


export const newAppStore = () => {
    const epicMiddleware = createEpicMiddleware({
        dependencies: {}
    })

    const store = createStore(
        appReducers,
        {},
        applyMiddleware(epicMiddleware)
    );

    epicMiddleware.run(appEpic)

    return store;
}