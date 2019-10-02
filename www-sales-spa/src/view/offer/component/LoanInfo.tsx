import {connect} from "react-redux";
import {ApplicationState, offerIdOf, offerVersionOf} from "../../app/store/AppState";
import {LoanInfoWc} from "../../../integration/loan/LoanInfoWc";

const mapStateToProps = ({ offerPage }: ApplicationState) => ({
    id: "loan-info",
    sourceId: offerIdOf(offerPage),
    sourceVersion: offerVersionOf(offerPage)
})

export const LoanInfo = connect(mapStateToProps)(LoanInfoWc)