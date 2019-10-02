import {connect} from "react-redux";
import {ApplicationState, offerIdOf, offerVersionOf} from "../../app/store/AppState";
import {LoanErrorWc} from "../../../integration/loan/LoanErrorWc";

const mapStateToProps = ({ offerPage }: ApplicationState) => ({
    id: "loan-error",
    sourceId: offerIdOf(offerPage),
    sourceVersion: offerVersionOf(offerPage)
})

export const LoanError = connect(mapStateToProps)(LoanErrorWc)